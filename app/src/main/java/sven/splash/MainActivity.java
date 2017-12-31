package sven.splash;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import sven.library.MistView;
import sven.library.SplashView;

public class MainActivity extends AppCompatActivity {

    private SplashView splashView;
    private MistView mistView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        splashView = (SplashView)findViewById(R.id.splash);
        mistView = (MistView) findViewById(R.id.mist);
        splashView.startAnimate();
        splashView.setOnEndListener(new SplashView.OnEndListener() {
            @Override
            public void onEnd(SplashView view) {
                view.setVisibility(View.GONE);
                mistView.setVisibility(View.VISIBLE);
                mistView.startAnimate(splashView.getDrawingCache());
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            splashView.startAnimate();
            splashView.setVisibility(View.VISIBLE);
            mistView.setVisibility(View.GONE);
        }
        return super.onTouchEvent(event);
    }
}
