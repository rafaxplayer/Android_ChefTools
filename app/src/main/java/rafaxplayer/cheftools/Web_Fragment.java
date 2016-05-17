package rafaxplayer.cheftools;


import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.recipes.fragments.NewEditRecipe_Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class Web_Fragment extends Fragment {
    private WebView web;
    private Button save;
    private Button cancel;
    private String url;
    private String urlimage = "http://www.google.com/advanced_image_search";
    private String urlgoogle = "https://www.google.com/";

    public Web_Fragment() {

        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_web, container, false);
        web = (WebView) v.findViewById(R.id.webView);
        save = (Button) v.findViewById(R.id.buttonsaveurl);
        cancel = (Button) v.findViewById(R.id.buttonCancelurl);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NewEditRecipe_Fragment) getActivity().getSupportFragmentManager().findFragmentByTag("neweditrecipe")).setUrl(web.getUrl());

                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        load_url(getActivity(), urlgoogle);
    }

    private void load_url(final Activity activity, final String Url) {

        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setBuiltInZoomControls(true);
        web.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                ((BaseActivity) activity).setTittleDinamic("Loading...");
                ((BaseActivity) activity).setProgress(progress * 100);

                if (progress == 100) {
                    ((BaseActivity) activity).setTittleDinamic(Url);
                }
            }
        });

        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                // Handle the error
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                super.onPageStarted(view, url, favicon);

            }

            @Override
            public void onReceivedLoginRequest(WebView view, String realm,
                                               String account, String args) {
                // TODO Auto-generated method stub
                super.onReceivedLoginRequest(view, realm, account, args);

            }

            @Override
            public void onLoadResource(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onLoadResource(view, url);
                Web_Fragment.this.url = url;

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if ("about:blank".equals(url) && view.getTag() != null) {
                    view.loadUrl(view.getTag().toString());
                } else {
                    view.setTag(url);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                view.loadUrl(url);
                return true;
            }
        });

        web.loadUrl(Url);

    }

}
