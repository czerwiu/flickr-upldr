# flickr-upldr
Flickr Uploader app

## Overview
This is a simple Java-based desktop application that allows users to upload photos to Flickr. 
It exposes web service endpoint for uploading image to the user's Flickr account.

The web service is secured using basic authentication. Username is provided via application runtime variable `flickr.user.name` and password is SHA-256 hash of the Flickr password provided via `flickr.user.pswd` runtime variable.

## Prerequisites

There are 7 parameters that must be provided via application runtime variables for the app to work:

- `flickr.api.key` - Flickr API key provided by Flickr for accessing their API
- `flickr.api.secret` - Flickr API secret provided by Flickr for accessing their API
- `flickr.oauth.token` - OAuth token provided by Flickr after user authorizes the app
- `flickr.oauth.token.secret` - OAuth token secret provided by Flickr after user authorizes the app
- `flickr.user.nsid` - Flickr user NSID (numeric identifier).
- `flickr.user.name` - Flickr username for basic authentication
- `flickr.user.pswd` - SHA-256 hash of the Flickr password for basic authentication


## Tech Stack

- Java 25
- Spring Boot 4.0.0
- Maven
- Flickr4Java library for Flickr API integration

## Web Service Endpoint

- `POST /upload` - Uploads a photo to Flickr. Requires album name to be specified. If the album does not exist, it will be created upon upload of the first photo.
  - Request Parameters:
    - `file` (multipart file) - The image file to upload
    - `album` (string) - Album name to add the photo to - required
    - `title` (string) - Title of the photo - optional
    - `description` (string) - Description of the photo - optional
    - `tags` (string) - Comma-separated tags for the photo - optional
  - Response:
    - 200 OK with success message
    - 400 Bad Request if parameters are missing
    - 500 Internal Server Error on upload failure

