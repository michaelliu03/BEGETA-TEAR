
Twitter开源了数据实时分析平台Heron。
Twitter使用Storm实时分析海量数据已经有好几年了，并在2011年将其开源。该项目稍后开始在Apache基金会孵化，并在2015年秋天成为顶级项目。Storm以季度为发布周期，并且向着人们期望的稳定版前进。但一直以来，Twitter都在致力于开发替代方案Heron，因为Storm无法满足他们的实时处理需求。
Twitter现在已经用Heron完全替换了Storm。前者现在每天处理“数10TB的数据，生成数10亿输出元组”，在一个标准的单词计数测试中，“吞吐量提升了6到14倍，元组延迟降低到了原来的五到十分之一”，硬件减少了2/3。