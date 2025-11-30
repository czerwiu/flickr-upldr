# flickr-upldr
Flickr Uploader app

## Overview
This is a simple Java-based desktop application that allows users to upload photos to Flickr. 
It exposes web service endpoint for uploading image to the user's Flickr account.

The web service is secured using basic authentication. Username is provided via env variable FLICKR_USERNAME, password encrypted with SHA-256 and provided via FLICKR_PWD_HASH.

## Prerequisites

There are 7 parameters that must be provided via env variables for the app to work:

- FLICKR_API_KEY - provided by Flickr for accessing their API
- FLICKR_API_SECRET - provided by Flickr for accessing their API
- FLICKR_OAUTH_TOKEN - provided by Flickr after user authorizes the app
- FLICKR_OAUTH_TOKEN_SECRET - provided by Flickr after user authorizes the app
- FLICKR_USER_NSID - the Flickr user NSID (numeric identifier)
- FLICKR_USERNAME - Flickr username
- FLICKR_PWD_HASH - SHA-256 hash of the Flickr password for basic auth

## Tech Stack

- Java 25
- Spring Boot 3.5.x
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

