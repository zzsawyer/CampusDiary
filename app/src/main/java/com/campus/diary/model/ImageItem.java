package com.campus.diary.model;

import java.io.Serializable;

/**
 * Created by Allen.Zeng on 2016/12/15.
 */
public class ImageItem implements Serializable
{
	private static final long serialVersionUID = -7188270558443739436L;
	public String imageId;
	public String thumbnailPath;
	public String sourcePath;
	public boolean isSelected = false;
}
