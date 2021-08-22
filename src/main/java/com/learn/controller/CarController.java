package com.learn.controller;

import com.learn.entity.CarEntity;
import com.learn.service.CarService;
import com.learn.utils.PageUtils;
import com.learn.utils.Query;
import com.learn.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 车位信息
 *
 */
@RestController
@RequestMapping("car")
public class CarController extends AbstractController {
	// 注入Service服务对象
	@Autowired
	private CarService carService;

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {

		if (super.getUserId() > 1)
			params.put("user", super.getUserId());

		// 查询列表数据
		Query query = new Query(params);

		List<CarEntity> carList = carService.queryList(query);
		int total = carService.queryTotal(query);

		PageUtils pageUtil = new PageUtils(carList, total, query.getLimit(), query.getPage());

		return R.ok().put("page", pageUtil);
	}

	/**
	 * 列表
	 */
	@RequestMapping("/list2")
	public R list2(@RequestParam Map<String, Object> params) {
		Query query = new Query(params);
		List<CarEntity> carList = carService.queryList(query);
		return R.ok().put("list", carList);
	}

	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	public R info(@PathVariable("id") Long id) {
		CarEntity car = carService.queryObject(id);

		return R.ok().put("car", car);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody CarEntity car) {

		carService.save(car);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody CarEntity car) {//前端传递给后端的请求体中的json参数，以较复杂的CarEntity对象接收前端传过来的json数据
		carService.update(car);
		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	public R delete(@RequestBody Long[] ids) {
		carService.deleteBatch(ids);
		return R.ok();
	}

}
