package site.bzyl.dto;

import lombok.Data;
import site.bzyl.domain.Dish;
import site.bzyl.domain.DishFlavor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO的作用：
 * 1. 省略实体的字段，减少内存开销or保护数据的隐秘性，例如返回没有密码的员工密码对象
 * 2. 扩展实体的字段，比如一个请求包含两个实体的字段，显然不能用两个@RequestBody来读取json
 *    只能通过dto对象把两个实体的字段在controller封装成一个对象读取，在service中分别处理
 */
@Data
public class DishDTO extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
