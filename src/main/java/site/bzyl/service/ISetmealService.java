package site.bzyl.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;
import site.bzyl.commom.Result;
import site.bzyl.dto.SetmealDTO;
import site.bzyl.entity.Setmeal;
import site.bzyl.entity.SetmealDish;

import java.util.List;

@Transactional
public interface ISetmealService extends IService<Setmeal> {
    Result<IPage> getPage(Integer page, Integer pageSize, String name);

    Result<String> saveSetmealWithDishes(SetmealDTO setmealDTO);

    Result<String> deleteByIds(List<Long> ids);

    Result<String> updateStatusByIds(Integer stat, String ids);

    Result<SetmealDTO> getSetmealDTOById(Long id);

    Result<String> updateBySetmealDTO(SetmealDTO setmealDTO);

    Result<List> listSetmeal(Setmeal setmeal);

    Result<List<SetmealDish>> setmealDishDetail(Long setmealId);
}
