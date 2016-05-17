package rafaxplayer.cheftools.Globalclasses;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageGalleryModel implements Parcelable {

    public String Title;
    public String ImagePath;
    public int ID;


    public ImageGalleryModel() {

    }

    public ImageGalleryModel(Parcel in) {
        readToParcel(in);
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    public void readToParcel(Parcel in) {
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
