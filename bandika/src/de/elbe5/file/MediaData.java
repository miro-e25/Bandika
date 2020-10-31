package de.elbe5.file;

import de.elbe5.base.data.BinaryFile;
import de.elbe5.request.SessionRequestData;

public class MediaData extends FileData {

    public MediaData() {
    }

    // multiple data

    @Override
    public void readSettingsRequestData(SessionRequestData rdata) {
        super.readSettingsRequestData(rdata);
        BinaryFile file = rdata.getFile("file");
        createFromBinaryFile(file);
        if (getDisplayName().isEmpty()) {
            setDisplayName(file.getFileNameWithoutExtension());
        }
        else{
            adjustFileNameToDisplayName();
        }
    }

}
