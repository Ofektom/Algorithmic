package com.algorithm.android.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Problem metadata for algorithm problems
 * This mirrors the ProblemInfo class from the console Main.java
 */
public class ProblemInfo implements Parcelable {
    public String name;
    public String[] aliases;
    public String description;
    public String shortDescription;
    public String[] keywords;
    public String category;
    public String className;
    public String[] methods;
    public String[] paramNames;
    public String[] paramTypes;
    public Object[] exampleInputs;

    public ProblemInfo(String name, String[] aliases, String description,
                      String shortDescription, String[] keywords, String category,
                      String className, String[] methods,
                      String[] paramNames, String[] paramTypes, Object[] exampleInputs) {
        this.name = name;
        this.aliases = aliases != null ? aliases : new String[]{};
        this.description = description != null ? description : "";
        this.shortDescription = shortDescription != null ? shortDescription : "";
        this.keywords = keywords != null ? keywords : new String[]{};
        this.category = category != null ? category : "";
        this.className = className;
        this.methods = methods;
        this.paramNames = paramNames;
        this.paramTypes = paramTypes;
        this.exampleInputs = exampleInputs;
    }

    // Parcelable implementation
    protected ProblemInfo(Parcel in) {
        name = in.readString();
        aliases = in.createStringArray();
        if (aliases == null) aliases = new String[]{};
        
        description = in.readString();
        if (description == null) description = "";
        
        shortDescription = in.readString();
        if (shortDescription == null) shortDescription = "";
        
        keywords = in.createStringArray();
        if (keywords == null) keywords = new String[]{};
        
        category = in.readString();
        if (category == null) category = "";
        
        className = in.readString();
        if (className == null) className = "";
        
        methods = in.createStringArray();
        if (methods == null) methods = new String[]{};
        
        paramNames = in.createStringArray();
        if (paramNames == null) paramNames = new String[]{};
        
        paramTypes = in.createStringArray();
        if (paramTypes == null) paramTypes = new String[]{};
        
        // Note: exampleInputs cannot be easily serialized, will be null
        exampleInputs = null;
    }

    public static final Creator<ProblemInfo> CREATOR = new Creator<ProblemInfo>() {
        @Override
        public ProblemInfo createFromParcel(Parcel in) {
            return new ProblemInfo(in);
        }

        @Override
        public ProblemInfo[] newArray(int size) {
            return new ProblemInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name != null ? name : "");
        dest.writeStringArray(aliases != null ? aliases : new String[]{});
        dest.writeString(description != null ? description : "");
        dest.writeString(shortDescription != null ? shortDescription : "");
        dest.writeStringArray(keywords != null ? keywords : new String[]{});
        dest.writeString(category != null ? category : "");
        dest.writeString(className != null ? className : "");
        dest.writeStringArray(methods != null ? methods : new String[]{});
        dest.writeStringArray(paramNames != null ? paramNames : new String[]{});
        dest.writeStringArray(paramTypes != null ? paramTypes : new String[]{});
        // exampleInputs not serialized
    }
}

