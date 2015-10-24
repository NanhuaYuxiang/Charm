package com.science.strangertofriend.fragment.dealWithListView;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.widget.Toast;

import com.avos.avoscloud.AVObject;

/**
 * ������ϵ���б���ظ���ʾ �� ��com.science.strangertofriend.fragment���У� AddressListFragment
 * .filledData()��������
 * 
 * @Description: TODO
 * @Create by lilin
 * @Date 2015��10��24�� . ����10:31:15
 * @Blog www.gaosililin.iteye.com
 * @E_mail gaosi0812@gmail.com
 * @School University of South China(USC)
 */

public class DealWithLstView {
	private AVObject dealWIthAVO;

	/**
	 * ������ϵ���б��ظ�������
	 * 
	 * @param list
	 *            ԭ����list����
	 * @return ���ش�����list����
	 */
	public List<AVObject> dealWithListView(List<AVObject> list) {
		List<AVObject> dealWithList = new ArrayList<>();
		for (AVObject avo : list) {
			// ��ȡ��ϵ�˵�email
			String email = avo.getString("friendEmail");
			// ����ϵ�˼ӵ������Ķ���
			dealWithList.add(avo);
			// ������õ���ϵ�˶��д���1ʱ��ִ��ɨ��ȥ��
			for (int i = 0; i < dealWithList.size() - 1; i++) {
				// ��ȡ�������е���ϵ��
				dealWIthAVO = dealWithList.get(i);
				// ��ȡ��ϵ�˶��е���ϵ�˵�email
				String dealWithEmail = dealWIthAVO.getString("friendEmail");
				if (email.equals(dealWithEmail)) {// �����ظ�����ϵ�ˣ��ɵ�
					// �����ظ���������޳����һ��
					dealWithList.remove(dealWithList.size() - 1);
					break;
				}
			}

		}

		return dealWithList;
	}

}
