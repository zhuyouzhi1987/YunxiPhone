package com.yunxi.phone.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.yunxi.phone.R;
import com.yunxi.phone.application.YunxiApplication;
import com.yunxi.phone.bean.Download;
import com.yunxi.phone.bean.DownloadMax;
import com.yunxi.phone.bean.MsgBean;
import com.yunxi.phone.bean.Task;
import com.yunxi.phone.bean.TaskContentBean;
import com.yunxi.phone.http.DownloadManager;
import com.yunxi.phone.service.DownloadService;
import com.yunxi.phone.utils.ACache;
import com.yunxi.phone.utils.AddressApi;
import com.yunxi.phone.utils.L;
import com.yunxi.phone.utils.TextUtil;
import com.yunxi.phone.utils.XUtil;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.common.Callback;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bond on 16/3/17.
 */
public class TiyanListAdapter extends ArrayListAdapter<TaskContentBean> {
    Context context;
    int task_id;
    int downloadType = 0;
    TaskContentBean infoBean;
    int progress = 0;
    ArrayList<Integer> list = new ArrayList<>();
    public Map<Integer, Integer> taskMap = new HashMap<>();

    public TiyanListAdapter(Context context) {
        this.context = context;

    }
    boolean tag=false;
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if (null == convertView) {
            viewHolder = new ViewHolder();
            LayoutInflater mInflater = LayoutInflater.from(context);
            convertView = mInflater.inflate(R.layout.task_list_item, null);

            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.desc = (TextView) convertView.findViewById(R.id.desc);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
            viewHolder.task_btn = (TextView) convertView.findViewById(R.id.task_btn);
            viewHolder.task_btn.setTag(position);
            convertView.setTag(viewHolder);
            AutoUtils.autoSize(convertView);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final ViewHolder test = viewHolder;

        infoBean = getItem(position);

        viewHolder.title.setText(infoBean.getTask_Name().toString());
        viewHolder.desc.setText(infoBean.getTask_Content().toString());
        Glide.with(context).load(infoBean.getTask_Icon()).centerCrop().placeholder(R.mipmap.placeholder_square).into(viewHolder.icon);
//        viewHolder.task_btn.setText(infoBean.getTask_Test_Price() + "");
        final int x = position;
        viewHolder.task_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test.task_btn.setEnabled(false);
                if (DownloadService.service != null) {
                    if (DownloadService.service.taskType.get(getItem(x).getTask_ID()) != null) {
                        downloadType = DownloadService.service.taskType.get(getItem(x).getTask_ID());
                    } else {
                        if (taskMap.get(getItem(x).getTask_ID()) != null) {
                            downloadType = taskMap.get(getItem(x).getTask_ID());
                        } else {
                            downloadType = 0;
                        }
                    }
                } else {
                    if (taskMap.get(getItem(x).getTask_ID()) != null) {
                        downloadType = taskMap.get(getItem(x).getTask_ID());
                    } else {
                        downloadType = 0;
                    }
                }
                L.d("downloadType: "+downloadType);
                switch (downloadType) {
                    case 1:
                    case 2:
                        test.task_btn.setEnabled(true);
                        taskMap.put(getItem(x).getTask_ID(), 0);
                        DownloadManager.getInstance().pause(getItem(x).getTask_Package_Url());
                        test.task_btn.setText("继续");
                        break;
                    case 3:
                        File myTempFile = new File(Environment.getExternalStorageDirectory()
                                + "/pf/apps/" + TextUtil.getMd5Value(getItem(x).getTask_Package_Url()) + ".apk");
                        openFile(myTempFile);
                        test.task_btn.setEnabled(true);
                        break;
                    case 5:
                        try {
                            //不需要参数也可以打开应用的方法，前提是应用必须规定好启动activity
                            Intent intent = context.getPackageManager().getLaunchIntentForPackage(getItem(x).getTask_Package_Name());
                            if (intent != null) {

                                context.startActivity(intent);
                                //打开应用完成任务
                                goTask(getItem(x).getTask_ID(), getItem(x).getTask_Package_Name());
                                //添加下载
                                addTask(getItem(x));
                            } else {
                                Toast.makeText(context, "无法打开应用", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(context, "无法打开应用，参数错误", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        try {
                            List<DownloadMax> downList = YunxiApplication.db.selector(DownloadMax.class).findAll();
                            if (downList != null && downList.size() > 0) {
                                for (DownloadMax max : downList) {
                                    if (max.getDownload_url().equals(TextUtil.getMd5Value(getItem(x).getTask_Package_Url()))) {
                                        //路径存在库中
                                        tag=true;
                                    }
                                }
                                if(!tag){
                                    if (downList.size() >=2) {
                                        tag=false;
                                        test.task_btn.setEnabled(true);
                                        Toast.makeText(context, "最多同时下载两个任务", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            }
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        tag=false;
                        taskMap.put(getItem(x).getTask_ID(), 1);
                        test.task_btn.setText(DownloadManager.getInstance().getProgress(getItem(x).getTask_Package_Url())+"%");
                        Intent intent = new Intent(context, DownloadService.class);
//                      Integer tag = (Integer) v.getTag();
//                      intent.putExtra("appfilename", getItem(x).getTask_Name());
                        addTask(getItem(x));
                        intent.putExtra("appfilename", TextUtil.getMd5Value(getItem(x).getTask_Package_Url()) + ".apk");
                        intent.putExtra("download_url", getItem(x).getTask_Package_Url());
                        intent.putExtra("ad_id", 1);
                        intent.putExtra("task_id", getItem(x).getTask_ID());
                        intent.putExtra("pachage_name", getItem(x).getTask_Package_Name());
                        intent.putExtra("isWho", 0);
                        context.startService(intent);
                        break;
                }
            }
        });
        initDownload(viewHolder, infoBean);
        return convertView;
    }

    private void initDownload(ViewHolder viewHolder, TaskContentBean infoBean) {
        if (DownloadService.service != null) {
            if (DownloadService.service.taskType.get(infoBean.getTask_ID()) != null) {
                downloadType = DownloadService.service.taskType.get(infoBean.getTask_ID());
            } else {
                downloadType = 0;
            }
        } else {
            downloadType = 0;
        }
        Download download = null;
        try {
            download = YunxiApplication.db.selector(Download.class).where("task_id", "=", infoBean.getTask_ID()).findFirst();
        } catch (DbException e) {
        }
        for (PackageInfo info : context.getPackageManager().getInstalledPackages(0)) {
            if (this.infoBean.getTask_Package_Name().equals(info.packageName)) {
                viewHolder.task_btn.setText("立即打开");
//                downloadType = 5;
                taskMap.put(infoBean.getTask_ID(), 5);
                if (DownloadService.service != null) {
                    if (DownloadService.service.taskType.get(infoBean.getTask_ID()) != null) {
                         DownloadService.service.taskType.put(infoBean.getTask_ID(),5);
                    }
                }
                return;
            }
        }
        L.d("downloadType: " + downloadType);
        switch (downloadType) {
            case 0:
                if (download != null) {
                    File myTempFile = new File(Environment.getExternalStorageDirectory()
                            + "/pf/apps/" + TextUtil.getMd5Value(this.infoBean.getTask_Package_Url()) + ".apk");

                    if (myTempFile.exists()) {
                        switch (download.getFinish()) {
                            case 0:
                                viewHolder.task_btn.setText("继续");
//                                progressBar.setProgress(download.getProgress());
//                                viewHolder.task_btn.setText(download.getProgress() + "");
                                break;
                            case 1:
//                                downloadType = 3;
                                taskMap.put(infoBean.getTask_ID(), 3);
                                if (DownloadService.service != null) {
                                    if (DownloadService.service.taskType.get(infoBean.getTask_ID()) != null) {
                                        DownloadService.service.taskType.put(infoBean.getTask_ID(),3);
                                    }
                                }
                                viewHolder.task_btn.setText("安装");
                                break;
                            default:
                                viewHolder.task_btn.setText("+"+infoBean.getTask_Test_Price()+"云朵");
                                break;
                        }
                    } else {
                        viewHolder.task_btn.setText("+"+infoBean.getTask_Test_Price()+"云朵");
                    }
                } else {
                    viewHolder.task_btn.setText("+"+infoBean.getTask_Test_Price()+"云朵");
                }
                break;
            case 1:
                viewHolder.task_btn.setText("0%");
                break;
            case 2:
                int progress = 0;
                if (download != null) {
                    progress = download.getProgress();
                }
                viewHolder.task_btn.setText(progress + "%");
//                progressBar.setProgress(progress);
                break;
            case 3:
                File myTempFile = new File(Environment.getExternalStorageDirectory()
                        + "/pf/apps/" + TextUtil.getMd5Value(this.infoBean.getTask_Package_Url()) + ".apk");
                if (myTempFile.exists()) {
                    viewHolder.task_btn.setText("安装");
                    taskMap.put(infoBean.getTask_ID(), 3);
                    if (DownloadService.service != null) {
                        if (DownloadService.service.taskType.get(infoBean.getTask_ID()) != null) {
                            DownloadService.service.taskType.put(infoBean.getTask_ID(),3);
                        }
                    }
                } else {
//                    downloadType = 0;
                    taskMap.put(infoBean.getTask_ID(), 0);
                    if (DownloadService.service != null) {
                        if (DownloadService.service.taskType.get(infoBean.getTask_ID()) != null) {
                            DownloadService.service.taskType.put(infoBean.getTask_ID(),0);
                        }
                    }
                    viewHolder.task_btn.setText("+"+infoBean.getTask_Test_Price()+"云朵");
                }
                break;
            case 4:
//                progress = download.getProgress();
//              progressBar.setProgress(progress);
                viewHolder.task_btn.setText("继续");
                taskMap.put(infoBean.getTask_ID(), 0);
                if (DownloadService.service != null) {
                    if (DownloadService.service.taskType.get(infoBean.getTask_ID()) != null) {
                        DownloadService.service.taskType.put(infoBean.getTask_ID(),0);
                    }
                }
                break;
            default:
                viewHolder.task_btn.setText("+"+infoBean.getTask_Test_Price()+"云朵");
//                downloadType = 0;
                taskMap.put(infoBean.getTask_ID(), 0);
                if (DownloadService.service != null) {
                    if (DownloadService.service.taskType.get(infoBean.getTask_ID()) != null) {
                        DownloadService.service.taskType.put(infoBean.getTask_ID(),0);
                    }
                }
                break;
        }
    }


    private static class ViewHolder {
        TextView title;
        TextView desc;
        ImageView icon;
        TextView task_btn;

    }


    public void updateItem(int index, ListView mListView, int downloadType, Intent intent) {
        this.downloadType = downloadType;
        if (mListView == null) {
            return;
        }
        // 获取当前可以看到的item位置
        int visiblePosition = mListView.getFirstVisiblePosition();
        // 如添加headerview后 firstview就是hearderview
        // 所有索引+1 取第一个view
        // View view = listview.getChildAt(index - visiblePosition + 1);
        // 获取点击的view
        View view = mListView.getChildAt(index - visiblePosition);
        if(null==view){
            return;
        }
        TextView task_btn = (TextView) view.findViewById(R.id.task_btn);

        switch (downloadType) {
            //开始
            case 1:
                task_btn.setEnabled(true);
                break;
            //下载中
            case 2:
                task_btn.setEnabled(true);
                progress = intent.getIntExtra("progress", 0);
                task_btn.setText(progress + "%");
//                progressBar.setProgress(progress);
                break;
            //完成
            case 3:
                task_btn.setEnabled(true);
                task_btn.setText("安装");
                break;
            //暂停
            case 4:
                task_btn.setEnabled(true);
                task_btn.setText("继续");
                break;
            //失败
            case -1:
                task_btn.setEnabled(true);
                taskMap.put(infoBean.getTask_ID(), 0);
                task_btn.setText("重试");
                break;
        }
    }

    private void openFile(File f) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        // 获得下载好的文件类型
        String type = getMIMEType(f);
        // 打开各种类型文件
        intent.setDataAndType(Uri.fromFile(f), type);
        // 安装
        context.startActivity(intent);
    }

    /**
     * 获得下载文件的类型
     *
     * @param f 文件名称
     * @return 文件类型
     */
    private String getMIMEType(File f) {
        String type = "";
        // 获得文件名称
        String fName = f.getName();
        // 获得文件扩展名
        String end = fName
                .substring(fName.lastIndexOf(".") + 1, fName.length())
                .toLowerCase();
        if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
                || end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
            type = "audio";
        } else if (end.equals("3gp") || end.equals("mp4")) {
            type = "video";
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
                || end.equals("jpeg") || end.equals("bmp")) {
            type = "image";
        } else if (end.equals("apk")) {
            type = "application/vnd.android.package-archive";
        } else {
            type = "*";
        }
        if (end.equals("apk")) {
        } else {
            type += "/*";
        }
        return type;
    }

    private void goTask(int task_id, String pachage_name) {
        Map<String, Object> map = new HashMap<>();
        ACache mCache = ACache.get(context);
        L.d("uid====" + mCache.getAsString("user_id"));
        map.put("user_id", mCache.getAsString("user_id"));
        map.put("userkey", mCache.getAsString("token"));
        map.put("task_id", task_id);
        map.put("package_name", pachage_name);
        map.put("type", 1);
        XUtil.Post(context, AddressApi.SEDN_TASK, map, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                L.d("GET_USER_TASK:" + result);
                MsgBean bean = JSON.parseObject(result,
                        MsgBean.class);
                if ("200".equals(bean.getResult())) {
                } else if ("500".equals(bean.getResult())) {
                    Toast.makeText(context, bean.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                L.d("onError:");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                L.d("onFinished:");
            }
        });
    }

    private void addTask(TaskContentBean bean) {
        try {
            YunxiApplication.db.delete(Task.class, WhereBuilder.b("task_id", "=", task_id));
            Task task = new Task();
            task.setAd_id(1);
            task.setTask_id(bean.getTask_ID());
            task.setPackagename(bean.getTask_Package_Name());
            task.setUrl(bean.getTask_Package_Url());
            YunxiApplication.db.save(task);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
