/*
 * Copyright (C) 2018 Darijo Barucic, Seventoes
 *
 *  Licensed under the MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.fablwesn.education.applepie.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fablwesn.education.applepie.R;
import com.fablwesn.education.applepie.data.models.StepsModel;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DetailsFragment extends Fragment {

    /*¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
      Bindings and Globals */

    public final static String KEY_STEP = "selected_step";
    public final static String KEY_PLAYER_PLAYS = "is_playing_exo";
    public static final String KEY_NUMB = "step_number";
    public static final String KEY_PLAYER_POS = "stopped_play_at";
    @BindView(R.id.text_description_steps)
    TextView descriptionText;
    @BindView(R.id.details_nav_text)
    TextView navigationText;
    @BindView(R.id.details_player_img)
    ImageView playerImage;
    @BindView(R.id.exo_player_steps)
    PlayerView exoPlayerView;
    @BindDrawable(R.drawable.ic_no_media)
    Drawable noMediaDraw;
    private Unbinder viewUnbind;
    private List<StepsModel> steps;
    private int stepSize;
    private int listPos;
    private Activity activity;
    private String videoUrl;
    private long playerPosition;
    private boolean isPlaying;
    private SimpleExoPlayer exoPlayer;
    private MediaSessionCompat mediaSession;
    private BandwidthMeter bandwidthMeter;
    private TrackSelector trackSelector;

    /*____________________________________________________________________________________________*/

    /*¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
       Fundamentals */

    public static DetailsFragment newInstance(List<StepsModel> steps, int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(KEY_STEP, new ArrayList<Parcelable>(steps));
        bundle.putInt(KEY_NUMB, position);

        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        viewUnbind = ButterKnife.bind(this, rootView);

        activity = getActivity();

        readBundle(getArguments());
        stepSize = steps.size();

        if (savedInstanceState != null) {
            listPos = savedInstanceState.getInt(KEY_NUMB);
            playerPosition = savedInstanceState.getLong(KEY_PLAYER_POS);
            isPlaying = savedInstanceState.getBoolean(KEY_PLAYER_PLAYS, false);
        }

        addData();

        if (savedInstanceState == null && videoUrl != null || !videoUrl.isEmpty()) {
            initMediaSession();
            initPlayer(Uri.parse(videoUrl));
        }

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (exoPlayer != null) {
            playerPosition = exoPlayer.getCurrentPosition();
            cleanPlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || exoPlayer == null && videoUrl != null && !videoUrl.isEmpty())) {
            initMediaSession();
            initPlayer(Uri.parse(videoUrl));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Free binded views.
        viewUnbind.unbind();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_NUMB, listPos);

        if (playerPosition != 0) {
            outState.putLong(KEY_PLAYER_POS, playerPosition);
        }
        outState.putBoolean(KEY_PLAYER_PLAYS, isPlaying);
    }

    /*____________________________________________________________________________________________*/

    /*¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
       Helper Methods */

    /**
     * Reads the bundle to get our model
     *
     * @param bundle saved
     */
    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            steps = bundle.getParcelableArrayList(KEY_STEP);
            listPos = bundle.getInt(KEY_NUMB);
        }
    }

    @OnClick(R.id.details_previous_img)
    public void loadPreviousStep() {
        listPos--;
        if (listPos < 0) {
            listPos = stepSize - 1;
        }
        if (exoPlayer != null) {
            exoPlayer.stop();
            playerPosition = 0;
        }
        addData();
        initMediaSession();
        initPlayer(Uri.parse(videoUrl));
    }

    @OnClick(R.id.details_next_img)
    public void loadNextStep() {
        listPos++;
        if (listPos > stepSize - 1) {
            listPos = 0;
        }
        if (exoPlayer != null) {
            exoPlayer.stop();
            playerPosition = 0;
        }

        addData();
        initMediaSession();
        initPlayer(Uri.parse(videoUrl));
    }

    /**
     * Adds data to the views
     */
    private void addData() {
        // Check for video
        videoUrl = steps.get(listPos).getVideoURL();

        if (videoUrl == null || videoUrl.isEmpty()) {
            exoPlayerView.setVisibility(View.GONE);
            playerImage.setVisibility(View.VISIBLE);

            String imageUrl = steps.get(listPos).getThumbnailURL();
            if (imageUrl == null || imageUrl.isEmpty()) {
                playerImage.setImageDrawable(noMediaDraw);
            } else {
                RequestOptions requestOptions = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.ic_loading)
                        .error(noMediaDraw);

                Glide.with(this).applyDefaultRequestOptions(requestOptions).load(imageUrl).into(playerImage);
            }
        } else {
            exoPlayerView.setVisibility(View.VISIBLE);
            playerImage.setVisibility(View.GONE);
        }

        String description = steps.get(listPos).getDescription();

        // Remove the steps inside the text if there is one
        if (Character.isDigit(description.charAt(0))) {
            descriptionText.setText(description.substring(2));
        } else {
            descriptionText.setText(description);
        }
        navigationText.setText(getString(R.string.navigation_details, listPos + 1, stepSize));
    }

    /*____________________________________________________________________________________________*/

    /*¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
       ExoPlayer */

    /**
     * Creates a mediasession to use
     */
    private void initMediaSession() {
        if (mediaSession == null) {
            mediaSession = new MediaSessionCompat(activity, DetailsFragment.class.getSimpleName());

            mediaSession.setFlags(
                    MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                            MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
            mediaSession.setMediaButtonReceiver(null);

            PlaybackStateCompat.Builder playStateBuilder = new PlaybackStateCompat.Builder()
                    .setActions(
                            PlaybackStateCompat.ACTION_PLAY |
                                    PlaybackStateCompat.ACTION_PAUSE |
                                    PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                    PlaybackStateCompat.ACTION_PLAY_PAUSE);

            mediaSession.setPlaybackState(playStateBuilder.build());
            mediaSession.setCallback(new MediaSessionCallback());
            mediaSession.setActive(true);
        }
    }

    /**
     * Creates our mediaplayer
     *
     * @param videoUrl of the video to play
     */
    private void initPlayer(Uri videoUrl) {
        if (exoPlayer == null) {

            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
            trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

            exoPlayer = ExoPlayerFactory.newSimpleInstance(activity, trackSelector);

            exoPlayerView.setPlayer(exoPlayer);

        }

        bandwidthMeter = new DefaultBandwidthMeter();
        String userAgent = Util.getUserAgent(activity, DetailsFragment.class.getSimpleName());
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(activity, userAgent);
        MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(videoUrl);

        exoPlayer.prepare(mediaSource);

        exoPlayer.setPlayWhenReady(isPlaying);
        exoPlayer.seekTo(playerPosition);

    }

    /**
     * Release the player, and deactivate media session
     */
    private void cleanPlayer() {
        if (exoPlayer != null) {
            playerPosition = exoPlayer.getCurrentPosition();
            isPlaying = exoPlayer.getPlayWhenReady();
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
        if (trackSelector != null) {
            trackSelector = null;
        }
        if (mediaSession != null) {
            mediaSession.setActive(false);
        }
    }

    /**
     * Allows external access through callbacks.
     */
    private class MediaSessionCallback extends MediaSessionCompat.Callback {

        @Override
        public void onPlay() {
            super.onPlay();
            exoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            super.onPause();
            exoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            exoPlayer.seekTo(0);
        }
    }

    /*____________________________________________________________________________________________*/
}
