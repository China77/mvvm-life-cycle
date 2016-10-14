/*
 * Copyright 2016 "Henry Tao <hi@henrytao.me>"
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.henrytao.mvvmlifecycle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import me.henrytao.mvvmlifecycle.rx.SubscriptionManager;
import me.henrytao.mvvmlifecycle.rx.UnsubscribeLifeCycle;
import rx.Subscription;

/**
 * Created by henrytao on 11/10/15.
 * Reference: http://developer.android.com/guide/components/fragments.html
 */
public abstract class MVVMFragment extends android.support.v4.app.Fragment implements MVVMLifeCycle, MVVMObserver {

  public abstract View onInflateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

  protected List<MVVMViewModel> mViewModels = new ArrayList<>();

  private List<MVVMViewModel> mAdapterViewModels = new ArrayList<>();

  private boolean mIsDestroy;

  private boolean mIsDestroyView;

  private boolean mIsPause;

  private boolean mIsStop;

  private Bundle mSavedInstanceState;

  private boolean mShouldRestoreInstanceState;

  private Constants.State mState;

  private SubscriptionManager mSubscriptionManager = new SubscriptionManager();

  @Override
  public void addAdapterViewModel(MVVMViewModel viewModel) throws IllegalAccessException {
    addViewModel(viewModel);
    if (!mAdapterViewModels.contains(viewModel)) {
      mAdapterViewModels.add(viewModel);
    }
    throw new IllegalAccessException("This method should be used with care");
  }

  @Override
  public void addViewModel(MVVMViewModel viewModel) {
    if (!mViewModels.contains(viewModel)) {
      mViewModels.add(viewModel);
      propagateLifeCycle(viewModel);
    }
  }

  @Override
  public void manageSubscription(UnsubscribeLifeCycle unsubscribeLifeCycle, Subscription... subscriptions) {
    for (Subscription subscription : subscriptions) {
      manageSubscription(subscription, unsubscribeLifeCycle);
    }
  }

  @Override
  public void manageSubscription(String id, Subscription subscription, UnsubscribeLifeCycle unsubscribeLifeCycle) {
    if ((unsubscribeLifeCycle == UnsubscribeLifeCycle.DESTROY && mIsDestroy) ||
        (unsubscribeLifeCycle == UnsubscribeLifeCycle.DESTROY_VIEW && mIsDestroyView) ||
        (unsubscribeLifeCycle == UnsubscribeLifeCycle.STOP && mIsStop) ||
        (unsubscribeLifeCycle == UnsubscribeLifeCycle.PAUSE && mIsPause)) {
      if (subscription != null && !subscription.isUnsubscribed()) {
        subscription.unsubscribe();
      }
      return;
    }
    mSubscriptionManager.manageSubscription(id, subscription, unsubscribeLifeCycle);
  }

  @Override
  public void manageSubscription(Subscription subscription, UnsubscribeLifeCycle unsubscribeLifeCycle) {
    if ((unsubscribeLifeCycle == UnsubscribeLifeCycle.DESTROY && mIsDestroy) ||
        (unsubscribeLifeCycle == UnsubscribeLifeCycle.DESTROY_VIEW && mIsDestroyView) ||
        (unsubscribeLifeCycle == UnsubscribeLifeCycle.STOP && mIsStop) ||
        (unsubscribeLifeCycle == UnsubscribeLifeCycle.PAUSE && mIsPause)) {
      if (subscription != null && !subscription.isUnsubscribed()) {
        subscription.unsubscribe();
      }
      return;
    }
    mSubscriptionManager.manageSubscription(subscription, unsubscribeLifeCycle);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mShouldRestoreInstanceState = true;
    mSavedInstanceState = savedInstanceState != null ? savedInstanceState : new Bundle();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    // dispatching
    for (MVVMViewModel viewModel : mViewModels) {
      viewModel.onActivityResult(requestCode, resultCode, data);
    }
  }

  @Override
  public void onCreate() {
    mState = Constants.State.ON_CREATE;
    mIsDestroy = false;
    // dispatching
    for (MVVMViewModel viewModel : mViewModels) {
      viewModel.onCreate();
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mViewModels = new ArrayList<>();
    onInitializeViewModels();
    onCreate();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return onInflateView(inflater, container, savedInstanceState);
  }

  public void onCreateView() {
    mState = Constants.State.ON_CREATE_VIEW;
    mIsDestroyView = false;
    // dispatching
    for (MVVMViewModel viewModel : mViewModels) {
      viewModel.onCreateView();
    }
  }

  @Override
  public void onDestroy() {
    mState = Constants.State.ON_DESTROY;
    mIsDestroy = true;
    mSubscriptionManager.unsubscribe(UnsubscribeLifeCycle.DESTROY);
    mSubscriptionManager.unsubscribe();
    super.onDestroy();
    // dispatching
    for (MVVMViewModel viewModel : mViewModels) {
      viewModel.onDestroy();
    }
    mViewModels.clear();
  }

  @Override
  public void onDestroyView() {
    mState = Constants.State.ON_DESTROY_VIEW;
    mIsDestroyView = true;
    mSubscriptionManager.unsubscribe(UnsubscribeLifeCycle.DESTROY_VIEW);
    super.onDestroyView();
    // dispatching
    for (MVVMViewModel viewModel : mViewModels) {
      viewModel.onDestroyView();
    }
    for (MVVMViewModel viewModel : mAdapterViewModels) {
      viewModel.onDestroy();
    }
    mViewModels.removeAll(mAdapterViewModels);
    mAdapterViewModels.clear();
  }

  @Override
  public void onPause() {
    mState = Constants.State.ON_PAUSE;
    mIsPause = true;
    mSubscriptionManager.unsubscribe(UnsubscribeLifeCycle.PAUSE);
    super.onPause();
    // dispatching
    for (MVVMViewModel viewModel : mViewModels) {
      viewModel.onPause();
    }
  }

  public void onRestoreInstanceState(Bundle savedInstanceState) {
    savedInstanceState = savedInstanceState != null ? savedInstanceState : new Bundle();
    // dispatching
    for (MVVMViewModel viewModel : mViewModels) {
      viewModel.onRestoreInstanceState(savedInstanceState);
    }
  }

  @Override
  public void onResume() {
    if (mShouldRestoreInstanceState) {
      mShouldRestoreInstanceState = false;
      onRestoreInstanceState(mSavedInstanceState);
    }
    mState = Constants.State.ON_RESUME;
    mIsPause = false;
    super.onResume();
    // dispatching
    for (MVVMViewModel viewModel : mViewModels) {
      viewModel.onResume();
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    // dispatching
    for (MVVMViewModel viewModel : mViewModels) {
      viewModel.onSaveInstanceState(outState);
    }
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onStart() {
    mState = Constants.State.ON_START;
    mIsStop = false;
    super.onStart();
    // dispatching
    for (MVVMViewModel viewModel : mViewModels) {
      viewModel.onStart();
    }
  }

  @Override
  public void onStop() {
    mState = Constants.State.ON_STOP;
    mIsStop = true;
    mSubscriptionManager.unsubscribe(UnsubscribeLifeCycle.STOP);
    super.onStop();
    // dispatching
    for (MVVMViewModel viewModel : mViewModels) {
      viewModel.onStop();
    }
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    onCreateView();
  }

  @Override
  public void unsubscribe(String id) {
    mSubscriptionManager.unsubscribe(id);
  }

  private void propagateLifeCycle(@NonNull MVVMViewModel viewModel) {
    if (mState == null) {
      return;
    }
    int n = Constants.STATES.indexOf(mState) + 1;
    for (int i = 0; i < n; i++) {
      switch (Constants.STATES.get(i)) {
        case ON_CREATE:
          viewModel.onCreate();
          break;
        case ON_CREATE_VIEW:
          viewModel.onCreateView();
          break;
        case ON_START:
          viewModel.onStart();
          break;
        case ON_RESUME:
          viewModel.onResume();
          break;
        case ON_PAUSE:
          viewModel.onPause();
          break;
        case ON_STOP:
          viewModel.onStop();
          break;
        case ON_DESTROY_VIEW:
          viewModel.onDestroyView();
          break;
        case ON_DESTROY:
          viewModel.onDestroy();
          break;
      }
    }
  }
}
