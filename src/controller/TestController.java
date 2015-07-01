package controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import model.EntPost;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import dao.EntPostDao;
import dao.JobfairDao;

@Controller
@RequestMapping("test/")
public class TestController {

	@Resource
	private JobfairDao jobfairDao;
	@Resource
	private EntPostDao entPostDao;

	@RequestMapping("dao.do")
	@ResponseBody
	public Map<String, Object> dao() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<EntPost> list = entPostDao.selectList("SELECT * FROM zcdh_ent_post LIMIT 0,10000");
		if (list.size() == 0)
			resultMap.put("success", false);
		else
			resultMap.put("success", true);
		return resultMap;
	}

}
