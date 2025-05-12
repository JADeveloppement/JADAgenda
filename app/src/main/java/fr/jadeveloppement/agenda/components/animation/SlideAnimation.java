package fr.jadeveloppement.agenda.components.animation;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

public class SlideAnimation {
    private final Context context;

    public SlideAnimation(Context c){
        this.context = c;
    }

    public void slideUp(View v){
        final int initialHeight = v.getHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(v.getLayoutParams().width, 1);
                    v.setLayoutParams(layoutParams);
                    v.requestLayout();
                    v.setVisibility(View.GONE);
                }else{
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(v.getLayoutParams().width, initialHeight - (int)(initialHeight * interpolatedTime));
                    v.setLayoutParams(layoutParams);
                    v.requestLayout();
                }
            }
        };

        // 1dp/ms
        a.setDuration(300);
        v.startAnimation(a);
    }

    public void slideDown(View v, int targetHeight){
        final int initialHeight = targetHeight;
        v.setVisibility(View.VISIBLE);

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(v.getLayoutParams().width, targetHeight);
                    v.setLayoutParams(layoutParams);
                    v.requestLayout();
                }else{
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(v.getLayoutParams().width, (int)(initialHeight * interpolatedTime) < 2 ? 1 : (int)(initialHeight * interpolatedTime));
                    v.setLayoutParams(layoutParams);
                    v.requestLayout();
                }
            }
        };

        // 1dp/ms
        a.setDuration(300);
        v.startAnimation(a);
    }
}
