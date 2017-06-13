package com.rabtman.acgclub.mvp.ui.activity;

import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import butterknife.BindView;
import com.rabtman.acgclub.R;
import com.rabtman.acgclub.base.constant.HtmlConstant;
import com.rabtman.acgclub.base.constant.IntentConstant;
import com.rabtman.acgclub.base.view.X5VideoWebView;
import com.rabtman.acgclub.base.view.X5VideoWebView.onChromeConsoleListener;
import com.rabtman.acgclub.di.component.DaggerScheduleVideoComponent;
import com.rabtman.acgclub.di.module.ScheduleVideoModule;
import com.rabtman.acgclub.mvp.contract.ScheduleVideoContract;
import com.rabtman.acgclub.mvp.presenter.ScheduleVideoPresenter;
import com.rabtman.common.base.BaseActivity;
import com.rabtman.common.di.component.AppComponent;
import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.export.external.interfaces.ConsoleMessage.MessageLevel;

/**
 * @author Rabtman
 */
public class ScheduleVideoActivity extends BaseActivity<ScheduleVideoPresenter> implements
    ScheduleVideoContract.View {

  @BindView(R.id.webview)
  X5VideoWebView webView;
  @BindView(R.id.progress_video)
  ProgressBar progressVideo;

  @Override
  protected void setupActivityComponent(AppComponent appComponent) {
    DaggerScheduleVideoComponent.builder()
        .appComponent(appComponent)
        .scheduleVideoModule(new ScheduleVideoModule(this))
        .build()
        .inject(this);
  }

  @Override
  protected int getLayout() {
    getWindow().setFormat(PixelFormat.TRANSLUCENT);
    try {
      if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 11) {
        getWindow()
            .setFlags(
                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
      }
    } catch (Exception e) {
    }
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    return R.layout.activity_browser;
  }

  @Override
  protected void initData() {
    //监听webview控制台日志
    webView.setOnChromeConsoleListener(new onChromeConsoleListener() {
      @Override
      public void onConsoleMessage(ConsoleMessage consoleMessage) {
        if (consoleMessage.messageLevel() == MessageLevel.ERROR) {
          showError(getString(R.string.msg_error_load_video));
          //onBackPressedSupport();
        }
      }
    });
    mPresenter.getScheduleVideo(getIntent().getStringExtra(IntentConstant.SCHEDULE_EPISODE_URL));
  }

  @Override
  protected void onDestroy() {
    webView.destroy();
    super.onDestroy();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    try {
      super.onConfigurationChanged(newConfig);
      if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
      } else if (getResources().getConfiguration().orientation
          == Configuration.ORIENTATION_PORTRAIT) {
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void hideLoading() {
    progressVideo.setVisibility(View.GONE);
  }

  private void hideSystemNavigationBar() {
    if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
      View view = this.getWindow().getDecorView();
      view.setSystemUiVisibility(View.GONE);
    } else if (Build.VERSION.SDK_INT >= 19) {
      View decorView = getWindow().getDecorView();
      int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
          | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
      decorView.setSystemUiVisibility(uiOptions);
    }
  }

  @Override
  public void showScheduleVideo(String videoUrl, String videoHtml) {
    webView.loadDataWithBaseURL(videoUrl, videoHtml, HtmlConstant.MIME_TYPE, HtmlConstant.ENCODING,
        null);
  }
}