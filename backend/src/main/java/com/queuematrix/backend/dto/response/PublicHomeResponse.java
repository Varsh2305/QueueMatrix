package com.queuematrix.backend.dto.response;

import java.util.List;

public class PublicHomeResponse {
    private String appName;
    private String heading;
    private String tagline;
    private String description;
    private List<CategoryInfo> supportedCategories;
    private List<String> features;

    public PublicHomeResponse() {}

    public PublicHomeResponse(String appName, String heading, String tagline, String description,
                              List<CategoryInfo> supportedCategories, List<String> features) {
        this.appName = appName;
        this.heading = heading;
        this.tagline = tagline;
        this.description = description;
        this.supportedCategories = supportedCategories;
        this.features = features;
    }

    public static class CategoryInfo {
        private String key;
        private String name;
        private String description;
        private String icon;

        public CategoryInfo() {}

        public CategoryInfo(String key, String name, String description, String icon) {
            this.key = key;
            this.name = name;
            this.description = description;
            this.icon = icon;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<CategoryInfo> getSupportedCategories() {
        return supportedCategories;
    }

    public void setSupportedCategories(List<CategoryInfo> supportedCategories) {
        this.supportedCategories = supportedCategories;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }
}
