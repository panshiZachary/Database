package com.example.databaseframework;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.databaseframework.adapter.MyAdapter;
import com.example.databaseframework.bean.Photo;
import com.example.databaseframework.bean.User;
import com.example.databaseframework.db.BaseDao;
import com.example.databaseframework.db.BaseDaoFactory;

import com.example.databaseframework.sub_sqlite.BaseDaoSubFactory;
import com.example.databaseframework.sub_sqlite.UserDao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ListView listView;
    private MyAdapter mAdapter;
    private List<User> mList;

    UserDao userDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.lv_user);

        mList = new ArrayList<>();
        mAdapter = new MyAdapter(this,mList);
        listView.setAdapter(mAdapter);

        userDao = BaseDaoFactory.getInstance().getBaseDao(UserDao.class, User.class);
    }
    int i = 0;

    /**
     * 登录返回数据
     * @param view
     */
    public void login(View view) {
        User user = new User();
        user.setId("p00"+(++i));
        user.setName("Zachary"+i);
        user.setPassword("123456");

        userDao.insert(user);
        Toast.makeText(this,"执行成功",Toast.LENGTH_LONG).show();
    }

    /**
     * 插入私库
     * @param view
     */
    public void subInsert(View view) {
        Photo photo = new Photo();
        photo.setPath("/data/data/myphoto.jpg");
        photo.setTime(new Date().toString());

        BaseDao subDao = BaseDaoSubFactory.getInstance().getSubDao(BaseDao.class, Photo.class);
        subDao.insert(photo);
        Toast.makeText(this,"插入一条数据",Toast.LENGTH_LONG).show();

    }



    public void insert(View view) {
        BaseDao<User> baseDao = BaseDaoFactory.getInstance().getBaseDao(BaseDao.class,User.class);
        baseDao.insert(new User("1","zachary","123456"));
        Toast.makeText(this,"插入数据成功",Toast.LENGTH_LONG).show();
    }

    public void update(View view) {
        BaseDao<User> baseDao = BaseDaoFactory.getInstance().getBaseDao(BaseDao.class,User.class);
        User user = new User();
        user.setName("abc");

        User where = new User();
        where.setName("zachary");
        

        baseDao.update(user,where);
        Toast.makeText(this,"更新数据成功",Toast.LENGTH_LONG).show();
    }

    public void delete(View view) {
        BaseDao<User> baseDao = BaseDaoFactory.getInstance().getBaseDao(BaseDao.class,User.class);

        User where = new User();
        where.setName("abc");

        baseDao.delete(where);
        Toast.makeText(this,"删除数据成功",Toast.LENGTH_LONG).show();
    }

    public void query(View view) {
        BaseDao<User> baseDao = BaseDaoFactory.getInstance().getBaseDao(BaseDao.class,User.class);
        User where = new User();
//        where.setName("zachary");

        List<User> list = baseDao.query(where);
        mList.clear();
        mList.addAll(list);
        mAdapter.notifyDataSetChanged();




    }




}
