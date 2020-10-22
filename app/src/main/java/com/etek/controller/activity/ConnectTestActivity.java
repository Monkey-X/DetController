package com.etek.controller.activity;


import android.os.Bundle;
import android.util.Log;
import com.etek.controller.R;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.widget.TableView;
import static com.etek.sommerlibrary.widget.TableView.MODE_ALL_UNIT_EVENT;

/**
 * 连接检测
 */
public class ConnectTestActivity extends BaseActivity {

    private TableView mTable;
    private String[][] mDetData;
    private int[][] mDetColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_test);
        initSupportActionBar(R.string.title_act_connect_state);
        initDate();
        initView();
    }

    /**
     * 初始化ViewR.id.
     */
    private void initView() {
        int width = getWindowWidth();
        mTable = findViewById(R.id.connect_state_table);
        mTable.setHeaderNames("序号", "管码", "孔位", "连接");
        width = width - 30;
        mTable.setColumnWidth(0, width / 6);
        mTable.setColumnWidth(1, width / 2);
        mTable.setColumnWidth(2, width / 6);
        mTable.setColumnWidth(3, width / 6);

        //设置项
        mTable.setmUnitTextColors(mDetColor);
        mTable.setUnitSelectable(false);//单元格处理事件的时候是否可以选中
        mTable.setUnitDownColor(R.color.red);//单元格处理事件的时候，按下态的颜色
        mTable.setTableData(mDetData);
        mTable.notifyAttributesChanged();
        mTable.setEventMode(MODE_ALL_UNIT_EVENT);

        mTable.setOnUnitClickListener(new TableView.OnUnitClickListener() {
            @Override
            public void onUnitClick(int row, int column, String unitText) {
                if (3 == column){
                    String[] rowData = mTable.getRowData(row);
                    showStatusDialog("是否对 " + rowData[1] + " 进行删除？");
                }

                Log.e("onItemClick","row: " + row + "   column:" + column + "   unitText: " + unitText);
            }
        });
    }

    /**
     * 页面展示的数据
     */
    private void initDate() {
        mDetData = new String[8][4];
        mDetColor = new int[8][4];
        for (int i = 0; i < 8; i++) {
            mDetData[i][0] = "" + (i+1);
            mDetColor[i][0] = R.color.black;
            mDetData[i][1] = "" + ("6000612500169");
            mDetColor[i][1] = R.color.black;
            mDetData[i][2] = "" + ("1-2");
            mDetColor[i][2] = R.color.black;
            mDetData[i][3] = "" + ("失败");
            mDetColor[i][3] = R.color.dimgray;
        }
    }
}