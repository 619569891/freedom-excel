# freedom-excel
JAVA POI 实现EXCEL 动态表头生成、动态添加数据（导入导出）、 Tree结构的遍历

我们目的是要建立一个EXCEL的工具类，它的功能：

1、可以生成动态表头，单级，多级都支持，尤其是树形表头（整体思路按照树形结构数据来遍历）；

2、数据可配置，支持动态填写数据（一个List结构的数据）；

3、读取Excel数据；

设计思路：需要一个含有树形结构的实体类Column，用于转换外部数据；而且该实体类要记录下它自己在EXCEL表中的坐标，以及跨行，跨列；接着用这个实体类来生成EXCEL表中的单元格cell。其中还需一个Tree工具类，它要处理Tree型结构数据；最终达到灵活导入导出Excel。

备注: tree根(最顶的节点root)的id必须为零。
