#  Mask项目简介

Chyohn Mask用于对敏感信息脱敏，比如用户名、银行卡号、身份照、邮箱、手机号等等。具有功能强大，使用方式简单的特点。
客户端代码只需要使用`MaskHandlerFactory`提供的工厂方法创建一个`IMaskHandler`对象即可对数据做脱敏。
你也可以扩展自己的`IMaskHandler`实现来添加更多的脱敏处理方式。


# 快速开始

## 引入maven依赖
```xml
<dependencies>
    <dependency>
        <groupId>io.github.chyohn.mask</groupId>
        <artifactId>mask</artifactId>
        <version>1.0.0-RELEASE</version>
    </dependency>
</dependencies>
```

## 使用示例

1. 保留手机号码开头3位和末尾4位
```java
IMaskHandler handler = MaskHandlerFactory.reserveOuter(3, 4);
String masked = handler.handle("13755556666");
// 137****6666
System.out.println(masked);
```

2. 保留中文姓氏
```java
IMaskHandler handler = MaskHandlerFactory.sizeSelector() // 创建一个选择器，根据长度选择脱敏处理器器
       .addHandler(MaskHandlerFactory.reserveLeft(1),2, 3) // 名字长度为2和3时，保留第一个字符
       .addHandler(MaskHandlerFactory.reserveLeft(2), 4); // 名字长度为4以上的，保留左边2个字符
        
System.out.println(handler.handle("李白")); // 李*
System.out.println(handler.handle("王昌龄")); // 王**
System.out.println(handler.handle("司马相如")); // 司马**
System.out.println(handler.handle("司马相如后代")); // 司马****
```

3. 邮箱脱敏

需求： 邮箱中`@`左边部分只保留前1个字符且脱敏字符固定长度为3，@右边部分全部也只保留`.`后面的内容，且脱敏部分只展示1个指定的脱敏字符: ^_^
```java
// @左边部分只保留前1个字符且只展示3个脱敏字符
IMaskHandler leftHandler = MaskHandlerFactory.reserveLeft(1).setMaskLength(3); 
// @右边部分处理器
IMaskHandler rightHandler = MaskHandlerFactory.group(".") // 以.做分割
       .addHandler(MaskHandlerFactory.maskAll().setMaskLength(1)); // .分割后第一部分全脱敏，且只展示1个指定的脱敏字符: ^_^

IMaskHandler handler = MaskHandlerFactory.group("@") // 以@分割
       .addHandler(leftHandler, rightHandler); // 第一部分使用左边左处理器，第二部分使用右处理器
System.out.println(handler.handle("abcdefddddddd@sina.com")); // a***@^_^.com
```

4. 模糊字符中的数字
```java
IMaskHandler handler = MaskHandlerFactory.maskNumber();
System.out.println(handler.handle("他的年龄35岁")); // 他的年龄**岁
```

5. 模糊字符串中的字母
```java
IMaskHandler andler = MaskHandlerFactory.maskLetter();
System.out.println(handler.handle("35到45岁是人生的golden年龄")); // 35到45岁是人生的******年龄
```

# MaskHandlerFactory使用说明

MaskHandlerFactory提供了一系列创建脱敏处理器的工厂方法，下面对这些方法做个概括：

## 1. 核心方法
为maskInner(int, int) 和 maskOuter(int, int)。所有工厂方法都在这两个方法上扩展

## 2. 根据脱敏的位置有以下工厂方法
   1. 脱敏指定位置的字符串： 以mask开头，有5个方法（左边、中间、右边、两边固定长度、两边可控长度）
   2. 保留指定位置的字符串：以reserve开头，有5个方法（左边、中间、右边、两边固定长度、两边可控长度）
   3. 拼接脱敏字符串到指定字符串位置：以append开头，有4个方法（左边、右边、两边固定脱敏字符串长度、两边可控脱敏字符串长度长度）
   4. 只脱敏字符串中的数字：maskNumber()和maskNumber(String)
   5. 只脱敏字符串中的字母：maskLetter()和maskLetter(String)
   6. 对字符串不脱敏：ignore()
   7. 对字符串全部脱敏：输出与原字符串长度相同maskAll()和输出固定长度的脱敏字符串maskAll(int)
   8. 对字符串全部隐藏：hideAll()，不展示任何字符串，即空串

## 3. 脱敏处理器组合
1. 分组脱敏，即对一个字符串不同部分使用不同的脱敏方式，工厂方法有group(String)和group(String, int)，方法的第一个参数为分隔符。
比如上面关于邮箱脱敏的例子。
2. 根据长度选择脱敏处理器，工厂方法sizeSelector() sizeSelector(String) 和 sizeSelector(String, int)，方法中第一个参数为分割符，
根据分割后的数组长度匹配脱敏处理器。另外如果没有分割符，就以字符串长度匹配脱敏处理器，比如上面关于姓名脱敏的例子。

## 4. 进阶

### 以数组元素为单位做脱敏
   
以上说的脱敏多少个字符或者保留多个字符，都是以字符串中字符个数来说的，还有些情况是把字符串按一定方式分割后的字符串数组个数来讨论脱敏或保留多少个元素。
比如上面邮箱脱敏为例，见下面rightHandler的构造方式的不同之处
```java
// @右边部分只保留第一个字符
IMaskHandler leftHandler = MaskHandlerFactory.reserveLeft(1);
// @右边部分使用点.分割后，第一部分全脱敏，且只展示1个指定的脱敏字符: ^_^
IMaskHandler rightHandler = MaskHandlerFactory.maskLeft(1).setSeparator(".").setMaskStr("^_^");
// 使用‘@’分隔后的字符串数组，第一个字符串用left脱敏，第二个字符串用right脱敏
IMaskHandler group = MaskHandlerFactory.group("@").addHandler(leftHandler, rightHandler);
System.out.println(group.handle("abcdefddddddd@sina.com")); // a***@^_^.com
```
rightHandler的代码说明：
- `setSeparator(".")`的意思就是： 对字符串使用`.`点分割成一个数组
- `maskLeft(1)`的意思是：对数组的第一个元素做脱敏
- `setMaskStr("^_^")`的意思就是： 使用字符串`^_^`替代数组中被脱敏的元素。

### 把脱敏处理器转json字符串进行保存

应用场景： 如果需要把`IMaskHandler`对象转为json保存起来，在需要的时候把json转为`IMaskHandler`来对数据做脱敏。

示例：

```java
// 配置脱敏处理器
IMaskHandler handler = MaskHandlerFactory.reserveOuter(3, 4);

// 转JSON字符串
String configJson = handler.toConfig();

// 把json字符串转为对象
IMaskHandler handlerFromJson = MaskHandlerFactory.fromConfig(configJson);

// 脱敏电话号码
String masked = handlerFromJson.handle("13755556666");
System.out.println(masked); // 137****6666
```

前提条件，需要引入jackson maven依赖，如下
```xml
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jdk8</artifactId>
    <version>[2.13.1,)</version>
</dependency>
```

## License

Chyohn Mask software is licensed under the Apache License Version 2.0. See the [LICENSE](https://github.com/chyohn/mask/blob/master/LICENSE) file for details.