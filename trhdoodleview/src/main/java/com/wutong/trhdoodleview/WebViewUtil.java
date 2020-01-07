package com.wutong.trhdoodleview;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * 1.优先使用本地缓存功能
 * 2.清理本地缓存
 * Created by jiuman on 2019/10/14.
 * 使用注意
 * 使用完调用 onDestroy
 * <p>
 * myWebView = new WebViewUtil(this);
 * ////        myWebView.setProgress(pb);
 * RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
 * myWebView.intoView(rl, params);
 * myWebView.loadUrl("https://www2.9man.com//syncshuxe//start.html?path=3-1");
 */

public class WebViewUtil {
    private String TAG = "trh" + this.getClass().getSimpleName();
    private WebView webView;
    private WeakReference<Context> webActivityReference;

    private WebViewListener webViewListener;


    /**
     * 仅供测试使用
     *
     * @return
     */
    public WebView getWebView() {
        return webView;
    }

    public WebViewUtil(Context context) {
        webActivityReference = new WeakReference<Context>(context);
        webView = new WebView(webActivityReference.get());
        initWebView();

    }

    public void setWebViewListener(WebViewListener webViewListener) {
        this.webViewListener = webViewListener;
    }

    private long startTime;
    private long endTime;

    private void initWebView() {
        webView.setBackgroundColor(0x00000000); // 设置背景色
        webView.setWebViewClient(new WebViewClient() {



            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                startTime = System.currentTimeMillis();
                Log.i(TAG, "onPageStarted: " + startTime);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (isPreLoad) {
                    onPause();
                }

                endTime = System.currentTimeMillis();
                Log.i(TAG, "onPageFinished: " + endTime);
                Log.i(TAG, "onPageFinished: 耗时s：" + (endTime - startTime) / 1000.0);
            }

            //            Android6.0以上断网和链接超时处理：重写WebViewClient的onReceivedError()方法
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                int errorCode = error.getErrorCode();
                Log.i(TAG, "onReceivedError: " + errorCode);
                if (errorCode == ERROR_HOST_LOOKUP || errorCode == ERROR_CONNECT || errorCode == ERROR_TIMEOUT) {

                    if (webViewListener != null)
                        webViewListener.errorListener();
                }
            }

            //            网页异常
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                int statusCode = errorResponse.getStatusCode();
                if (404 == statusCode || 500 == statusCode) {
                    Log.i(TAG, "onReceivedHttpError: " + statusCode);
                    if (webViewListener != null)
                        webViewListener.errorListener();
                }

            }

            //不调用系统的浏览器
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
//设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
//缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

//其他细节操作
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        webView.setWebChromeClient(new MyWebChromeClient());
    }


  public void setProgress(ProgressBar pb){
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
//                if (newProgress >= 99) {
//                    pb.setVisibility(View.GONE);
//                } else {
//                    if (pb.getVisibility() == View.GONE) {
//                        pb.setVisibility(View.VISIBLE);
//                    }
//                    pb.setProgress(newProgress);
//                }
            }
        });
    }
    boolean isPreLoad = false;

    public void preLoadUrl(String url) {
        isPreLoad = true;
        webView.loadUrl(url);
    }


    public void loadUrl(String url) {
        onResume();
        isPreLoad = false;
//        Log.i(TAG,  "loadUrl: CacheMode"+ webView.getSettings().get);
        webView.loadUrl(url);
    }



    public void intoView(ViewGroup parent, ViewGroup.LayoutParams params) {
        parent.addView(webView, params);
    }

    private void onPause() {
        webView.onPause();
        webView.pauseTimers();
    }

    private void onResume() {
        webView.resumeTimers();
        webView.onResume();
    }
    /**
     * 注意调用完后 还需要将弱引用的context清理掉
     * this.webActivityReference.clear();
     * this.webActivityReference = null;
     */
    public void onDestroy() {
        //防止webView内存泄漏
        if (webView != null) {
            //先从父容器中移除webview,然后再销毁webview
            ViewParent parent = webView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(webView);
            }
            webView.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            webView.getSettings().setJavaScriptEnabled(false);
            webView.clearHistory();
            webView.removeAllViews();
            try {
                webView.destroy();
            } catch (Throwable ex) {
            }
            webView = null;
            this.webActivityReference.clear();
            this.webActivityReference = null;
        }
    }

    public void setCache() {

        Context context = webActivityReference.get();
        if (context == null) {
            Log.e(TAG, "clearWebViewCache: context页面估计已经销毁");
            return;
        }
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);  //设置 缓存模式
        // 开启 DOM storage API 功能
        settings.setDomStorageEnabled(true);
        //开启 database storage API 功能
        settings.setDatabaseEnabled(true);
        String cacheDirPath = context.getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME;
        //      String cacheDirPath = getCacheDir().getAbsolutePath()+Constant.APP_DB_DIRNAME;
        Log.i(TAG, "cacheDirPath=" + cacheDirPath);
        //设置数据库缓存路径
        settings.setDatabasePath(cacheDirPath);
        //设置  Application Caches 缓存目录
        settings.setAppCachePath(cacheDirPath);
        //开启 Application Caches 功能
        settings.setAppCacheEnabled(true);

    }


    private static final String APP_CACAHE_DIRNAME = "/webcache";

    /**
     * 清除WebView缓存
     */
    public void clearWebViewCache() {
        Context context = webActivityReference.get();
        if (context == null) {
            Log.e(TAG, "clearWebViewCache: context页面估计已经销毁");
            return;
        }
        //清理WebView缓存数据库
        try {
            context.deleteDatabase("webview.db");
            context.deleteDatabase("webviewCache.db");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //WebView 缓存文件
        File appCacheDir = new File(context.getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME);
        Log.e(TAG, "appCacheDir path=" + appCacheDir.getAbsolutePath());

        File webviewCacheDir = new File(context.getCacheDir().getAbsolutePath() + "/webviewCache");
        Log.e(TAG, "webviewCacheDir path=" + webviewCacheDir.getAbsolutePath());

        //删除webview 缓存目录
        if (webviewCacheDir.exists()) {
            deleteFile(webviewCacheDir);
        }
        //删除webview 缓存 缓存目录
        if (appCacheDir.exists()) {
            deleteFile(appCacheDir);
        }
    }

    /**
     * 递归删除 文件/文件夹
     *
     * @param file
     */
    public void deleteFile(File file) {

        Log.i(TAG, "delete file path=" + file.getAbsolutePath());

        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        } else {
            Log.e(TAG, "delete file no exists " + file.getAbsolutePath());
        }
    }


    class MyWebChromeClient extends WebChromeClient {

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            Log.i(TAG, "onConsoleMessage：["+consoleMessage.messageLevel()+"] "+ consoleMessage.message() + "(" +consoleMessage.sourceId()  + ":" + consoleMessage.lineNumber()+")");
            return super.onConsoleMessage(consoleMessage);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (webViewListener != null)
                webViewListener.loadProgressChangeListener(newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                if (title.contains("404") || title.contains("500") || title.contains("Error")) {
                    Log.i(TAG, "onReceivedTitle: " + title);
                    if (webViewListener != null)
                        webViewListener.errorListener();
                }
            }
        }
    }

    public interface WebViewListener {
        //        WebView加载的URL失败
        void errorListener();

        //        加载进度
        void loadProgressChangeListener(int progress);


    }

}