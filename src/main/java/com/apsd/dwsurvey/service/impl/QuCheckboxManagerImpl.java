package com.apsd.dwsurvey.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.apsd.dwsurvey.dao.QuCheckboxDao;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apsd.common.plugs.page.Page;
import com.apsd.common.plugs.page.PropertyFilter;
import com.apsd.common.service.BaseServiceImpl;
import com.apsd.dwsurvey.entity.QuCheckbox;
import com.apsd.dwsurvey.service.QuCheckboxManager;

/**
 * 多选题
 * @author  Perye
 *
 *
 *
 */
@Service("quCheckboxManager")
public class QuCheckboxManagerImpl extends BaseServiceImpl<QuCheckbox, String> implements QuCheckboxManager {

	@Autowired
	private QuCheckboxDao quCheckboxDao;
	
	@Override
	public void setBaseDao() {
		this.baseDao=quCheckboxDao;
	}
	
	public List<QuCheckbox> findByQuId(String quId){
		Page<QuCheckbox> page=new Page<QuCheckbox>();
		page.setOrderBy("orderById");
		page.setOrderDir("asc");
		
		List<PropertyFilter> filters=new ArrayList<PropertyFilter>();
		filters.add(new PropertyFilter("EQS_quId", quId));
		filters.add(new PropertyFilter("EQI_visibility", "1"));
		return findAll(page, filters);
	}
	
	
	public int getOrderById(String quId){
		Criterion criterion=Restrictions.eq("quId", quId);
		QuCheckbox quCheckbox=quCheckboxDao.findFirst("orderById", false, criterion);
		if(quCheckbox!=null){
			return quCheckbox.getOrderById();
		}
		return 0;
	}
	
	/*******************************************************************8
	 * 更新操作
	 */
	
	@Override
	@Transactional
	public QuCheckbox upOptionName(String quId,String quItemId, String optionName) {
		if(quItemId!=null && !"".equals(quItemId)){
			QuCheckbox quCheckbox=quCheckboxDao.get(quItemId);
			quCheckbox.setOptionName(optionName);
			quCheckboxDao.save(quCheckbox);	
			return quCheckbox;
		}else{
			//取orderById
			int orderById=getOrderById(quId);
			//新加选项
			QuCheckbox quCheckbox=new QuCheckbox();
			quCheckbox.setQuId(quId);
			quCheckbox.setOptionName(optionName);
			//title
			quCheckbox.setOrderById(++orderById);
			quCheckbox.setOptionTitle(orderById+"");
			quCheckboxDao.save(quCheckbox);
			return quCheckbox;
		}
	}
	
	@Override
	@Transactional
	public List<QuCheckbox> saveManyOptions(String quId,List<QuCheckbox> quCheckboxs) {
		//取orderById
		int orderById=getOrderById(quId);
		for (QuCheckbox quCheckbox : quCheckboxs) {
			//新加选项
			quCheckbox.setOrderById(++orderById);
			quCheckbox.setOptionTitle(orderById+"");
			quCheckboxDao.save(quCheckbox);
		}
		return quCheckboxs;
	}
	
	@Override
	@Transactional
	public void ajaxDelete(String quItemId) {
		QuCheckbox quCheckbox=get(quItemId);
		quCheckbox.setVisibility(0);
		quCheckboxDao.save(quCheckbox);
	}
	
	@Override
	@Transactional
	public void saveAttr(String quItemId, String isNote) {
		QuCheckbox quCheckbox=get(quItemId);
		if(isNote!=null && "1".equals(isNote)){
			quCheckbox.setIsNote(1);
		}else{
			quCheckbox.setIsNote(0);
		}
		quCheckboxDao.save(quCheckbox);
	}

}
