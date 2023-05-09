package site.bzyl.dto;
import lombok.Data;
import site.bzyl.entity.Setmeal;
import site.bzyl.entity.SetmealDish;

import java.util.List;

@Data
public class SetmealDTO extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
