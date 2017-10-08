# Account 更新日志
* 2017年3月-4月15日 
> 无记录

* 2017年4月16日
> 调整show_outcome控件的高度
> 调整show_outcome显示字体的大小
> 调整viewpager控件所在布局的高度
> 添加viewpager底部导航点(addOnPageChangeListener)
> 抽取PagerAdapter为MyPagerAdapter类
> 添加describe页面，从outcome页面的describe按钮点击进入
> 设置describe页面返回按钮响应事件，储存按钮响应事件(onBackPressed)
> 解决describe页面和outcome页面传值问题(intent)
> 解决describe页面中edittext无边框问题(xml)
> 将describe页面中edittext光标移至最后(setSelection)
> 滑动菜单添加分隔线

* 2017年4月17日
> 解决计算器显示输出时的部分bug
新增MyDatabaseHelper类用于创建数据库
新增MyDatabaseDeal类用于操作数据库
新增Data实体类用于封装Outcome储存的数据
OutcomeActivity新增data()方法获取数据并储存(type获取需新建一个HashMap对应id值与名称)

* 2017年4月18日
> 删除MyDatabaseDeal类
> 删除Data类
> 建立数据库Account.db
> 建立数据表Data储存支出数据
> 新增对话框用于当金额或者类别未选择时弹出的提示

* 2017年4月23日
> 新增MainFragment类作为主界面填充ListView及显示金额的容器
> 新增MyBaseAdapter类作为主界面ListView适配器(数据类型为Data)
> 新增MyFragmentPagerAdapter类作为主界面ViewPager说的适配器
> 新增main_viewpager布局文件
> 新增main_listview_item布局文件作为主界面ListView的item
> 新增DataBean实体类用于封装数据
> 新增ToolBar动态添加日期
> 新增滑动菜单展开与关闭时，ToolBar动态切换日期与标题
> 取消ViewPager和Fragment动态添加逻辑，改为界面创建时填装200个Fragment，并设置当前位置为99(当前日期)
> 优化主界面ListView(复用历史缓存对象convertView及添加ViewHolder)
> 解决ListView重复加载显示数据问题(在ListView onCreateView时将List的内容清除)
> 新增ListView显示数据库数据功能

* 2017年4月25日
> 新增OutcomeActivity页面储存的数据与主界面的ListView同步更新
> 删除OutcomeActivity页面中使用SimpleDateFormat获取日期的方式，更改为由MainActivity的intent传入当前日期
> 新增滑动菜单打开时不能点击Toolbar按钮的功能
> 新增OutcomeActivity页面account对话框(自定义dialog)

* 2017年4月26日
> 新增OutcomeActivity页面fixed_charged对话框
> 新增自定义对话框MyDialog类，合并account对话框与fixed_charge对话框

* 2017年4月27日
> 新增OutcomeDetailsActivity类作为主界面ListView点击Item后跳转到的支出详情页面
> 新增OutcomeDetailsActivity的布局activity_outcome_details

* 2017年4月28日
> 修改account列表为listView，以便数据更新
> 修改当金额或者类别未选择时弹出的提示对话框的样式

* 2017年4月29日
> 删除OutcomeActivity页面dialog()方法
> 将OutcomeActivity页面所有弹出对话框整合为由MyDialog全部实现
> 将OutcomeActivity页面中account和fixed_charge对话框ListView选中的Item回传到页面显示
> 修改主界面ListViwe的Item显示布局
> 修改initeTypeMap()中值为中文显示
> 新增主界面ListView点击Item跳转到支出详情页面时传递DataBean对象(DataBean实现Parcelable接口)
> 调整activity_outcome_details布局整体布局和显示
> 新增OutcomeDetailsActivity接收intent传递的对象并将相应数据显示到界面上
> 新增支出详情页面编辑按钮跳转到OutcomeActivity页面的相应逻辑
> OutcomeActivity页面新增ifEdit()方法用于执行当从支出详情页面跳转过来后的相应数据显示的逻辑
> OutcomeActivity页面新增valueToKey()方法用于哈希表值找键
> 解决跳转后跳转页面后RadioButton控件空指针问题
> OutcomeActivity页面新增dataBack()方法用于执行更新数据操作
> 解决回传支出详情页面和回传主界面bug问题
> 删除dataBack()方法，整合到data()方法中，并加入跳转判断

* 2017年4月30日
> 新增主界面日期选择器(DatePicker)，实现主界面菜单按钮弹出日期选择器功能
> 解决datePicker选择之后主界面ViewPager显示错误问题

* 2017年5月1日
> 解决datePicker选择之后更新数据问题
> 修改datePicker的确认按钮为滑动日期之后才可点击
> 完善datePicker与其他界面之间的逻辑
> 解决datePicker与Fragment滑动后日期不一致问题
> 解决datePicker弹出时，外部区域可点击问题
> 大量替换图标并加入部分未加入的图标
> MyBaseAdapter类中新增initTypeMap()方法用于type与图片的转换
> 新增主界面Listiew的Item显示图片
> 调整主界面Listiew的Item图片显示位置
> 删除OutcomeActivity类中的typeMap变量，initTypeMap()方法，valueToKey()方法
> 删除MyBaseAdapter类中的initTypeMap()方法
> 将所有与Map有关的方法整合为TypeMap类
> 修改OutcomeActivity类中储存DataBean时type值的获取方式为通过TypeMap类获得
> 修改OutcomeActivity类中查找type为通过TypeMap类获得
> 修改MyBaseAdapter类中type图片的获取方式为通过TypeMap类获得
> 调整ListView的Item的IamgeView大小，优化显示效果
> 新增主界面底部布局(具体功能未实现)
> 新增主界面总消费金额的显示(具体功能未实现)

* 2017年5月2日
> 解决OutcomeActivity类ifEdit()bug(RadioButton未判断属于哪个ViewPager)
> 调整主界面底部布局
> 更换OutcomeActivity布局show_outcome控件为AutofitTextView
> 调整show_outcome控件字体大小为60sp，typeface为serif
> 删除OutcomeActivity类对金额显示限制的相应逻辑
> 新增budget_setting布局作为预算编列页面布局
> 新增BudgetSettingActivity类作为预算编列页面
> 新增主界面底部布局点击功能
> 新增budget_calculator布局作为BudgetSettingActivity页面的计算器
> 修改OutcomeActivity页面计算器布局
> 修改BudgetSettingActivity页面计算器布局
> 新增BudgetSettingActivity页面跳转回主界面功能，并回传数据
> 新增主界面显示BudgetSettingActivity回传数据功能，且不足4位时自动补齐
> 更换main_viewpager布局total_cost控件为AutofitTextView
> 新增主界面总花费金额的显示
> 优化主界面总花费金额的显示逻辑，在无小数位时去除小数点
> 优化主界面ListView每项Item的金额显示逻辑，在无小数位时去除小数点

* 2017年5月3日
> 解决OutcomeActivity页面异常退出窗体泄露问题
> 解决BudgetSettingActivity页面异常退出窗体泄露问题
> 解决无法修改带小数的消费项目的bug(存入数据库时会自动补齐精度，将money数据类型更改为String)
> 删除主界面在无小数位时去除小数点的优化(改为String类型后无需优化)
> 新增主界面剩余预算金额的显示
> 更换主界面布局surplus_budget控件为AutofitTextView
> 更换主界面布局total_budget控件为AutofitTextView
> 更换支出详情界面布局outcome_details_money控件为AutofitTextView
> 优化支出详情界面金额显示效果
> 优化主界面预算总额与剩余预算的显示效果
> 新增主界面剩余预算百分比的显示
> 优化主界面预算显示逻辑
> 解决OutcomeActivity页面计算器数学运算逻辑bug问题
> 新增dialog_delete布局作为删除提示对话框
> MyDialog类新增删除提示对话框的逻辑
> 新增支出详情页面支持删除功能

* 2017年5月4日
> 整合所有颜色到color.xml
> 新增IncomeAcitivity类作为收入页面
> 新增IncomeDetailsActivity类作为收入详情页面
> 新增activity_income布局作为IncomeAcitivity类的布局
> 新增activity_income_details布局作为IncomeDetailsActivity类的布局
> 新增main_income_listview_item布局
> 新增main_income_viewpager布局
> 整合大部分outcome与income共同的逻辑

* 2017年5月5日
> 数据库表加入behavior列用于判断行为属于支出或收入
> 修改所有有关数据库操作的代码
> 整合drawable大部分selector
> 整合MyFragmen中预算显示相关逻辑到budget()方法中>

* 2017年5月6日
> 重制收入支出与预算的大部分逻辑
> 修复显示bug
> 新增OutcomeActivity页面和IncomeActivity页面RadioButton点击后弹出计算器
> 修改账户ListView选项

* 2017年5月11日
> DataBean新增id成员变量并修改相关代码
> 解决收入相同条目修改和删除时bug问题
> 解决支出相同条目修改和删除时bug问题
> 调整dialog_delete布局按钮之间的距离及按钮与文字之间的距离
> 修复剩余预算，总消费/收入显示bug，优化显示逻辑
> 新增SettingActivity类
> 新增activity_setting布局文件
> 新增主界面滑动菜单设置按钮跳转功能
> 新增Setting页面预算编列按钮跳转功能

* 2017年10月
> 更换listView为RecyclerView
> 设置autoFitTextView限制字数为20
> 调整viewpager为无限循环，取消加载200个fragment，节省资源
> 减少代码量，优化内存
> 修复话费对应文字出错的bug
> 新增主界面滑动菜单打开时，第一次点击返回键先关闭滑动菜单
> 新增支出页面点击账户选择之后，若未选择并关闭之后保留上次选择选项
