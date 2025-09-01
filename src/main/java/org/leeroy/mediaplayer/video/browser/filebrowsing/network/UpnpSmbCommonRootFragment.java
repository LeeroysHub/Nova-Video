// Copyright 2017 LeeroyFlix
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.leeroy.mediaplayer.video.browser.filebrowsing.network;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import org.leeroy.filecorelibrary.FileUtils;
import org.leeroy.mediaplayer.video.browser.BrowserCategory;
import org.leeroy.mediaplayer.video.R;
import org.leeroy.mediaplayer.video.browser.filebrowsing.network.SmbBrowser.BrowserBySmb;
import org.leeroy.mediaplayer.video.browser.filebrowsing.network.UpnpBrowser.BrowserByUpnp;

/**
 * Created by alexandre on 08/06/15.
 */
public abstract class UpnpSmbCommonRootFragment extends NewRootFragment implements WorkgroupShortcutAndServerAdapter.OnShareOpenListener{
    public UpnpSmbCommonRootFragment(){
        super();

    }
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        ((WorkgroupShortcutAndServerAdapter)mAdapter).setOnShareOpenListener(this);
    }
    @Override
    public void onShareOpen(WorkgroupShortcutAndServerAdapter.GenericShare share) {
        final Uri uri = Uri.parse(share.getUri());
        Bundle args = new Bundle();
        args.putParcelable(BrowserByNetwork.CURRENT_DIRECTORY, uri);
        args.putString(BrowserByNetwork.TITLE, share.getName());
        args.putString(BrowserByNetwork.SHARE_NAME, FileUtils.getName(uri));
        Fragment f;
        if (uri.getScheme().equals("smb")) {
            f = new BrowserBySmb();
            f.setArguments(args);
        } else {
            f = new BrowserByUpnp();
            f.setArguments(args);
        }
        BrowserCategory category = (BrowserCategory) getActivity().getSupportFragmentManager().findFragmentById(R.id.category);
        category.startContent(f);
    }

}
