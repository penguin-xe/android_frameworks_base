/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.wm.shell;

import android.os.IBinder;
import android.os.RemoteException;
import android.view.SurfaceControl;
import android.window.IRemoteTransition;
import android.window.IRemoteTransitionFinishedCallback;
import android.window.TransitionInfo;
import android.window.WindowContainerTransaction;

/**
 * {@link IRemoteTransition} for testing purposes.
 * Stores info about
 * {@link #startAnimation(IBinder, TransitionInfo, SurfaceControl.Transaction,
 * IRemoteTransitionFinishedCallback)} being called.
 */
public class TestRemoteTransition extends IRemoteTransition.Stub {
    private boolean mCalled = false;
    final WindowContainerTransaction mRemoteFinishWCT = new WindowContainerTransaction();

    @Override
    public void startAnimation(IBinder transition, TransitionInfo info,
            SurfaceControl.Transaction startTransaction,
            IRemoteTransitionFinishedCallback finishCallback)
            throws RemoteException {
        mCalled = true;
        finishCallback.onTransitionFinished(mRemoteFinishWCT, null /* sct */);
    }

    @Override
    public void mergeAnimation(IBinder transition, TransitionInfo info,
            SurfaceControl.Transaction t, IBinder mergeTarget,
            IRemoteTransitionFinishedCallback finishCallback) throws RemoteException {
    }

    /**
     * Check whether this remote transition
     * {@link #startAnimation(IBinder, TransitionInfo, SurfaceControl.Transaction,
     * IRemoteTransitionFinishedCallback)} is called
     */
    public boolean isCalled() {
        return mCalled;
    }
}
