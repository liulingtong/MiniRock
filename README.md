# MiniRock设计文档

### 特性

1. 兼容 redis 命令
2. 基于多个 provider 进行存储，例如，文件，内存，hdfs，redis 等只要可以读写二进制的地方
3. mvcc
4. 内存作为缓存，写请求同步，读请求内存读
5. 对象友好，基于对象情况，进行二进制结构设计，每个类型对象一个实例

### 默认实现设计

1. 二进制结构
    1. 分元数据文件 meta.obj 和数据文件 data.obj 
    2. 元数据文件使用 hashmap 存储，数据文件按顺序存储
    3. 读取时，先看文件lastmodify，如果未改变，则使用缓存的元数据，如果
    4. 元数据文件map的 key使用对象的 key ， 值为 list ， list 中按时间顺序插入数据，最新数据为最后一版，包含数据文件中的 offset，size，version，
    5. 数据文件每次更新后，更新元数据文件（第一版）
    6. 后台运行，清理过期数据，删除 5 分钟（可配置）之前元数据，并将数据文件对应位置清零后，进行合并。
2. 常用操作
    1. 查询：meta.obj查询元数据找到最新版本，找到偏移量和大小，在 data.obj中顺序读取，然后反序列化
    2. 插入：没有 key 的时候，执行插入逻辑，新增数据大小的 bytebuffer append 到文件末尾
    3. 更新：有 key 的时候，将数据 append 到文件末尾，并在元数据 list 中新增数据。
    4. 删除：将元数据标记为删除，在后台合并过程中，将元数据及元数据对应位置的数据置 0，并合并。