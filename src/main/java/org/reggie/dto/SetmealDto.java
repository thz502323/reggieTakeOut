package org.reggie.dto;

import lombok.Data;
import java.util.List;
import org.reggie.entity.Setmeal;
import org.reggie.entity.SetmealDish;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
