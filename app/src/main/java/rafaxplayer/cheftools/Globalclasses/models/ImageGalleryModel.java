package rafaxplayer.cheftools.Globalclasses.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageGalleryModel implements Parcelable {

    public static final Creator<ImageGalleryModel> CREATOR = new Creator<ImageGalleryModel>() {
        @Override
        public ImageGalleryModel createFromParcel(Parcel in) {
            return new ImageGalleryModel(in);
        }

        @Override
        public ImageGalleryModel[] newArray(int size) {
            return new ImageGalleryModel[size];
        }
    };
    public String Title;
    public String ImagePath;
    public int ID;

    public ImageGalleryModel() {

    }

    private ImageGalleryModel(Parcel in) {
        readToParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private void readToParcel(Parcel in) {
        Title = in.readString();
        ImagePath = in.readString();
        ID = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Title);
        dest.writeString(ImagePath);
        dest.writeInt(ID);
    }
}
