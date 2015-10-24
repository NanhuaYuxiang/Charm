package com.science.strangertofriend.fragment.dealWithListView;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.widget.Toast;

import com.avos.avoscloud.AVObject;

/**
 * 处理联系人列表的重复显示 ， 在com.science.strangertofriend.fragment包中， AddressListFragment
 * .filledData()方法调用
 * 
 * @Description: TODO
 * @Create by lilin
 * @Date 2015年10月24日 . 下午10:31:15
 * @Blog www.gaosililin.iteye.com
 * @E_mail gaosi0812@gmail.com
 * @School University of South China(USC)
 */

public class DealWithLstView {
	private AVObject dealWIthAVO;

	/**
	 * 处理联系人列表重复的数据
	 * 
	 * @param list
	 *            原生的list数据
	 * @return 返回处理后的list数据
	 */
	public List<AVObject> dealWithListView(List<AVObject> list) {
		List<AVObject> dealWithList = new ArrayList<>();
		for (AVObject avo : list) {
			// 获取联系人的email
			String email = avo.getString("friendEmail");
			// 将联系人加到处理后的队列
			dealWithList.add(avo);
			// 当处理好的联系人队列大于1时候，执行扫描去重
			for (int i = 0; i < dealWithList.size() - 1; i++) {
				// 获取处理后队列的联系人
				dealWIthAVO = dealWithList.get(i);
				// 获取联系人队列的联系人的email
				String dealWithEmail = dealWIthAVO.getString("friendEmail");
				if (email.equals(dealWithEmail)) {// 出现重复的联系人，干掉
					// 出现重复的情况，剔除最后一个
					dealWithList.remove(dealWithList.size() - 1);
					break;
				}
			}

		}

		return dealWithList;
	}

}
