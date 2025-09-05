// Copyright 2022 Courville Software
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

package org.leeroy.mediacenter.video.leanback.adapter.object;

import android.net.Uri;

import org.leeroy.filecorelibrary.FileUtils;
import org.leeroy.filecorelibrary.contentstorage.ContentStorageFileEditor;
import org.leeroy.filecorelibrary.ftp.FtpFileEditor;
import org.leeroy.filecorelibrary.jcifs.JcifsFileEditor;
import org.leeroy.filecorelibrary.localstorage.LocalStorageFileEditor;
import org.leeroy.filecorelibrary.sftp.SftpFileEditor;
import org.leeroy.filecorelibrary.zip.ZipFileEditor;
import org.leeroy.mediacenter.video.R;
import org.leeroy.mediacenter.video.utils.VideoUtils;

import java.io.Serializable;

public class GenericNetworkShortcut extends Shortcut implements Serializable {

    public GenericNetworkShortcut(long id, String fullPath, String name, String friendlyUri) {
        super(id, fullPath, friendlyUri, name);
    }

    public Uri getUri() {
        return Uri.parse(mFullPath);
    }

    @Override
    public int getImage() {
        Uri uri = getUri();
        return VideoUtils.getShortcutImageLeanback(uri);
    }
}
