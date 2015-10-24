package com.science.strangertofriend.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import yalantis.com.sidemenu.interfaces.ScreenShotable;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.pedant.SweetAlert.SweetAlertDialog;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avoscloud.leanchatlib.activity.ChatActivity;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnSwipeListener;
import com.science.strangertofriend.R;
import com.science.strangertofriend.adapter.SortAdapter;
import com.science.strangertofriend.adapter.SwingBottomInAnimationAdapter;
import com.science.strangertofriend.bean.SortModel;
import com.science.strangertofriend.fragment.dealWithListView.DealWithLstView;
import com.science.strangertofriend.ui.ChatRoomActivity;
import com.science.strangertofriend.ui.FriendInformationActivity;
import com.science.strangertofriend.utils.AVService;
import com.science.strangertofriend.utils.CharacterParser;
import com.science.strangertofriend.utils.ClearEditText;
import com.science.strangertofriend.utils.PinyinComparator;
import com.science.strangertofriend.widget.SideBar;
import com.science.strangertofriend.widget.SideBar.OnTouchingLetterChangedListener;

/**
 * @description ͨѶ¼����
 * 
 * @author ����Science ������
 * @school University of South China
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @2015-4-25
 * 
 */

public class AddressListFragment extends Fragment implements ScreenShotable,
		OnRefreshListener {

	private View mContainerView;
	private Bitmap mBitmap;
	private View mRootView;
	private Boolean mAdapterFlag = false;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	public int i = -1;

	private SwipeMenuListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private SortAdapter adapter;
	private ClearEditText mClearEditText;

	/**
	 * ����ת����ƴ������
	 */
	private CharacterParser characterParser;
	private List<SortModel> SourceDateList;

	/**
	 * ����ƴ��������ListView�����������
	 */
	private PinyinComparator pinyinComparator;

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		this.mContainerView = view.findViewById(R.id.address_container);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		mRootView = inflater.inflate(R.layout.address_list_fragment, container,
				false);

		initView();
		getAddressList();
		initListener();

		return mRootView;
	}

	private void initView() {

		// ʵ��������תƴ����
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();

		sideBar = (SideBar) mRootView.findViewById(R.id.sidrbar);
		dialog = (TextView) mRootView.findViewById(R.id.dialog);
		sideBar.setTextView(dialog);

		sortListView = (SwipeMenuListView) mRootView
				.findViewById(R.id.country_lvcountry);
		mClearEditText = (ClearEditText) mRootView
				.findViewById(R.id.filter_edit);

		// ˢ�³�ʼ��
		mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView
				.findViewById(R.id.swipe_address);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorSchemeResources(
				android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);

	}

	// �õ�ͨѶ¼�б�
	private void getAddressList() {

		AVQuery<AVObject> query = new AVQuery<AVObject>("AddressList");
		query.whereEqualTo("currentUser", AVUser.getCurrentUser().getUsername());
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(List<AVObject> list, AVException e) {
				if (list != null && list.size() != 0) {
					mAdapterFlag = true;
					SourceDateList = filledData(list);

					initData();

				} else {
					Toast.makeText(getActivity(), "�㻹û�к���ร�",
							Toast.LENGTH_SHORT).show();
					mAdapterFlag = false;
				}
			}
		});
	}

	private void initData() {

		// ����a-z��������Դ����
		Collections.sort(SourceDateList, pinyinComparator);
		adapter = new SortAdapter(getActivity(), SourceDateList);

		// ��̬�б�
		SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
				adapter);
		swingBottomInAnimationAdapter.setAbsListView(sortListView);
		sortListView.setAdapter(swingBottomInAnimationAdapter);

		// item����ɾ��
		initSwipeMenu();
	}

	private void initSwipeMenu() {
		// step 1. create a MenuCreator
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "open" item
				SwipeMenuItem openItem = new SwipeMenuItem(getActivity());
				// set item background
				openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
						0xCE)));
				// set item width
				openItem.setWidth(dp2px(90));
				// set item title
				// openItem.setTitle("��ϸ");
				// set item title fontsize
				// openItem.setTitleSize(18);
				// set item title font color
				// openItem.setTitleColor(Color.WHITE);
				openItem.setIcon(R.drawable.ic_detials);
				// add to menu
				menu.addMenuItem(openItem);

				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				// set item width
				deleteItem.setWidth(dp2px(90));
				// set a icon
				deleteItem.setIcon(R.drawable.ic_delete);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};

		// set creator
		sortListView.setMenuCreator(creator);

		// step 2. listener item click event
		sortListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				switch (index) {
				case 0:
					// open
					deatils(position);
					break;
				case 1:
					// delete
					// delete(item);
					reomveDialog(position);

					break;
				}
			}
		});

		// set SwipeListener
		sortListView.setOnSwipeListener(new OnSwipeListener() {

			@Override
			public void onSwipeStart(int position) {
				// swipe start
			}

			@Override
			public void onSwipeEnd(int position) {
				// swipe end
			}
		});

		// other setting
		// listView.setCloseInterpolator(new BounceInterpolator());

		// test item long click
		sortListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@SuppressLint("ShowToast")
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(
						getActivity(),
						"��ã�����"
								+ ((SortModel) adapter.getItem(position))
										.getName(), Toast.LENGTH_SHORT).show();
				return false;
			}
		});

		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				open(position);
			}
		});
	}

	private void reomveDialog(final int position) {

		new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
				.setTitleText("ȷ��ɾ������?")
				.setCancelText("ȡ��")
				.setConfirmText("ȷ��")
				.showCancelButton(true)
				.setCancelClickListener(
						new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {
								// reuse previous dialog instance, keep
								// widget user state, reset them if you need
								sDialog.setTitleText("��ȡ��!")
										.setConfirmText("OK")
										.showCancelButton(false)
										.setCancelClickListener(null)
										.setConfirmClickListener(null)
										.changeAlertType(
												SweetAlertDialog.SUCCESS_TYPE);
							}
						})
				.setConfirmClickListener(
						new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(final SweetAlertDialog sDialog) {
								sDialog.dismiss();

								final SweetAlertDialog nAlertDialog = new SweetAlertDialog(
										getActivity(),
										SweetAlertDialog.PROGRESS_TYPE);
								nAlertDialog.setTitleText("����Ŭ��ɾ��")
										.setContentText("���Ժ�");
								nAlertDialog.show();
								nAlertDialog.setCancelable(false);
								new CountDownTimer(800 * 4, 800) {
									public void onTick(long millisUntilFinished) {
										colorProgress(nAlertDialog);
									}

									public void onFinish() {
										i = -1;
										nAlertDialog.dismiss();
										reomveAddressItem(((SortModel) adapter
												.getItem(position)).getName());
										SourceDateList.remove(position);
										adapter.notifyDataSetChanged();
									}
								}.start();
							}
						}).show();

	}

	// ��������ɫ
	private void colorProgress(SweetAlertDialog pDialog) {
		i++;
		switch (i) {
		case 0:
			pDialog.getProgressHelper().setBarColor(
					getResources().getColor(android.R.color.holo_blue_bright));
			break;

		case 1:
			pDialog.getProgressHelper().setBarColor(
					getResources().getColor(android.R.color.holo_green_light));
			break;
		case 2:
			pDialog.getProgressHelper().setBarColor(
					getResources().getColor(android.R.color.holo_orange_light));
			break;

		case 3:
			pDialog.getProgressHelper().setBarColor(
					getResources().getColor(android.R.color.holo_red_light));
			break;
		}
	}

	// ɾ����Ϣitem
	private void reomveAddressItem(String friendName) {
		AVQuery<AVObject> query = new AVQuery<AVObject>("AddressList");
		query.whereEqualTo("friend", friendName);
		query.findInBackground(new FindCallback<AVObject>() {

			@Override
			public void done(final List<AVObject> list, AVException e) {
				if (list != null && list.size() != 0) {
					// ���̷߳�������
					new Thread(new Runnable() {

						@Override
						public void run() {
							AVService.removeFriends(list.get(list.size() - 1)
									.getObjectId());
						}
					}).start();
				}
			}
		});
	}

	// �����������
	private void deatils(int position) {

		Intent intent = new Intent(getActivity(),
				FriendInformationActivity.class);
		intent.putExtra("username",
				((SortModel) adapter.getItem(position)).getName());
		intent.putExtra("email",
				((SortModel) adapter.getItem(position)).getEmail());
		intent.putExtra("gender",
				((SortModel) adapter.getItem(position)).getGender());

		startActivity(intent);
	}

	// �����������
	private void open(final int position) {
		// Toast.makeText(getActivity(), "This is " + position,
		// Toast.LENGTH_SHORT)
		// .show();
		ChatManager chatManager = ChatManager.getInstance();
		chatManager.setupDatabaseWithSelfId(AVUser.getCurrentUser()
				.getUsername());
		chatManager.openClientWithSelfId(AVUser.getCurrentUser().getUsername(),
				new AVIMClientCallback() {
					@Override
					public void done(AVIMClient avimClient, AVException e) {
						if (e != null) {
							e.printStackTrace();
						}
						System.out
								.print("---------------AddressListFragment:open:"
										+ e.getMessage());
						final ChatManager chatManager = ChatManager
								.getInstance();
						chatManager.fetchConversationWithUserId(
								((SortModel) adapter.getItem(position))
										.getName(),
								new AVIMConversationCreatedCallback() {
									@Override
									public void done(
											AVIMConversation conversation,
											AVException e) {
										if (e != null) {
											System.out.print("e.getMessage()"
													+ e.getMessage());
										} else {
											chatManager
													.registerConversation(conversation);
											Intent intent = new Intent(
													getActivity(),
													ChatRoomActivity.class);
											intent.putExtra(
													ChatActivity.CONVID,
													conversation
															.getConversationId());
											intent.putExtra("position",
													position);
											startActivityForResult(intent, 1);

										}
									}
								});
					}
				});
	}

	/**
	 * ���ҵ�ǰ�û�������Ѳ���ʾ��messageҳ��
	 * 
	 * @param position
	 */
	private void findMessageListFriend(final String message, final int position) {

		AVQuery<AVObject> query = new AVQuery<AVObject>("MessageList");
		query.whereEqualTo("currentUser", AVUser.getCurrentUser().getUsername());
		query.whereEqualTo("friend",
				((SortModel) adapter.getItem(position)).getName());
		query.findInBackground(new FindCallback<AVObject>() {

			@SuppressLint("SimpleDateFormat")
			@Override
			public void done(final List<AVObject> list, AVException e) {

				// ����ʱ��
				Date date = new Date();
				SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
				final String sendTime = format.format(date);

				if (list != null && list.size() != 0) {
					// ���̷߳�������
					new Thread(new Runnable() {

						@Override
						public void run() {
							AVService.updateMessageList(
									list.get(list.size() - 1).getObjectId(),
									sendTime, message);
						}
					}).start();
				} else {

					// ���̷߳�������
					new Thread(new Runnable() {

						@Override
						public void run() {

							// ������Ϣ
							AVService.messageList(((SortModel) adapter
									.getItem(position)).getName(),
									((SortModel) adapter.getItem(position))
											.getAvaterUrl(), AVUser
											.getCurrentUser().getUsername(),
									sendTime, message);
						}
					}).start();
				}
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {
			if (resultCode == -1) {

				String messsage = data.getStringExtra("messsage");
				int position = data.getIntExtra("position", 0);
				findMessageListFriend(messsage, position);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void initListener() {

		// �����Ҳഥ������
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {

				if (mAdapterFlag) {
					// ����ĸ�״γ��ֵ�λ��
					int position = adapter.getPositionForSection(s.charAt(0));
					if (position != -1) {
						sortListView.setSelection(position);
					}
				}

			}
		});

		// �������������ֵ�ĸı�����������
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (mAdapterFlag) {
					// ������������ֵΪ�գ�����Ϊԭ�����б�����Ϊ���������б�
					filterData(s.toString());
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	/**
	 * ΪListView�������
	 * 
	 * @param date
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	private List<SortModel> filledData(List<AVObject> list) {

		List<SortModel> mSortList = new ArrayList<SortModel>();

		 List<AVObject> list1 = new DealWithLstView().dealWithListView(list);
		AVObject avObject = list1.get(0);

		for (AVObject avo : list1) {
			System.out.println(avo.getString("friendEmail"));

			SortModel sortModel = new SortModel();

			/*
			 * ͨѶ¼�б��������
			 */
			sortModel.setName(avo.getString("friend"));
			// ����ת����ƴ��
			String pinyin = characterParser.getSelling(avo.getString("friend"));
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// ������ʽ���ж�����ĸ�Ƿ���Ӣ����ĸ
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			/*
			 * ����ͷ��
			 */
			sortModel.setAvaterUrl(avo.getString("friendAvaterUrl"));
			/*
			 * ����Email
			 */
			sortModel.setEmail(avo.getString("friendEmail"));
			/*
			 * �����Ա�
			 */
			sortModel.setGender(avo.getString("friendGender"));

			mSortList.add(sortModel);
		}
		return mSortList;

	}

	/**
	 * ����������е�ֵ���������ݲ�����ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<SortModel> filterDateList = new ArrayList<SortModel>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = SourceDateList;
		} else {
			filterDateList.clear();
			for (SortModel sortModel : SourceDateList) {
				String name = sortModel.getName();
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}

		// ����a-z��������
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}

	@Override
	public void onRefresh() {
		getAddressList();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mSwipeRefreshLayout.setRefreshing(false);
			}
		}, 3800);
	}

	@Override
	public void takeScreenShot() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Bitmap bitmap = Bitmap.createBitmap(mContainerView.getWidth(),
						mContainerView.getHeight(), Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(bitmap);
				mContainerView.draw(canvas);
				AddressListFragment.this.mBitmap = bitmap;
			}
		}, 0);
		// Thread thread = new Thread() {
		// @Override
		// public void run() {
		// Bitmap bitmap = Bitmap.createBitmap(mContainerView.getWidth(),
		// mContainerView.getHeight(), Bitmap.Config.ARGB_8888);
		// Canvas canvas = new Canvas(bitmap);
		// mContainerView.draw(canvas);
		// AddressListFragment.this.mBitmap = bitmap;
		// }
		// };
		// thread.start();
	}

	@Override
	public Bitmap getBitmap() {
		return mBitmap;
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

}
