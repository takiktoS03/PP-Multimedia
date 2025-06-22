using Aplikacja_desktopowa.Model;
using Google.Apis.Storage.v1.Data;
using Google.Cloud.Firestore;
using Google.Cloud.Firestore.V1;
using Google.Cloud.Storage.V1;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Drawing.Imaging;
using System.IO;
using System.Net.Http;
using System.Threading.Tasks;

namespace Aplikacja_desktopowa.Service
{
    public class PhotoService
    {
        private readonly FirestoreDb _firestore;

        public PhotoService()
        {
            _firestore = FirebaseConfig.GetFirestoreDb();
        }

        public async Task AddPhotoAsync(string id, PhotoMetadata photo)
        {
            photo.UploadedAt = DateTime.UtcNow;
            DocumentReference docRef = _firestore.Collection("photos").Document(id);
            await docRef.SetAsync(photo);
        }

        public async Task<PhotoMetadata> GetPhotoAsync(string id)
        {
            DocumentReference docRef = _firestore.Collection("photos").Document(id);
            DocumentSnapshot snapshot = await docRef.GetSnapshotAsync();

            if (snapshot.Exists)
            {
                return snapshot.ConvertTo<PhotoMetadata>();
            }

            return null;
        }

        public async Task<List<PhotoMetadata>> GetAllPhotosAsync()
        {
            Query query = _firestore.Collection("photos");
            QuerySnapshot snapshot = await query.GetSnapshotAsync();

            List<PhotoMetadata> result = new List<PhotoMetadata>();
            foreach (DocumentSnapshot doc in snapshot.Documents)
            {
                if (doc.Exists)
                {
                    result.Add(doc.ConvertTo<PhotoMetadata>());
                }
            }

            return result;
        }

        public async Task<List<PhotoMetadata>> GetPhotosByIdsAsync(List<string> photoIds)
        {
            var result = new List<PhotoMetadata>();
            foreach (var id in photoIds)
            {
                var photo = await GetPhotoAsync(id);
                if (photo != null)
                    result.Add(photo);
            }
            return result;
        }

        public async Task<List<PhotoMetadata>> GetPhotosByUserIdAsync(string userId)
        {
            var collectionRef = _firestore.Collection("photos");
            var query = collectionRef.WhereEqualTo("user_id", userId);
            var snapshot = await query.GetSnapshotAsync();

            var photos = new List<PhotoMetadata>();

            foreach (var document in snapshot.Documents)
            {
                if (document.Exists)
                {
                    var photo = document.ConvertTo<PhotoMetadata>();
                    photo.Id = document.Id;
                    photos.Add(photo);
                }
            }

            return photos;
        }

        public async Task<string> AddPhotoAndGetIdAsync(PhotoMetadata photo, string localFilePath)
        {
            string fileNameInStorage = Guid.NewGuid().ToString() + Path.GetExtension(localFilePath);
            string url = await UploadPhotoToStorageAsync(localFilePath, fileNameInStorage);

            photo.FilePath = url;
            photo.UploadedAt = DateTime.UtcNow;

            var docRef = _firestore.Collection("photos").Document();
            photo.Id = docRef.Id;
            await docRef.SetAsync(photo);
            return docRef.Id;
        }

        public async Task<string> UploadPhotoToStorageAsync(string localFilePath, string fileNameInStorage)
        {
            string bucketName = "image-management-cbaee.firebasestorage.app";
            var storage = StorageClient.Create();
            Google.Apis.Storage.v1.Data.Object storageObject;


            using (var fileStream = File.OpenRead(localFilePath))
            {
                storageObject = await storage.UploadObjectAsync(
                    bucket: bucketName,
                    objectName: $"photos/{fileNameInStorage}",
                    contentType: null,
                    source: fileStream
                //options: new UploadObjectOptions { PredefinedAcl = PredefinedObjectAcl.PublicRead }
                );
            }

            string token = null;
            if (storageObject.Metadata != null && storageObject.Metadata.ContainsKey("firebaseStorageDownloadTokens"))
                token = storageObject.Metadata["firebaseStorageDownloadTokens"];

            string url = $"https://firebasestorage.googleapis.com/v0/b/{bucketName}/o/photos%2F{System.Uri.EscapeDataString(fileNameInStorage)}?alt=media";
            if (!string.IsNullOrEmpty(token))
                url += $"&token={token}";

            return url;
        }

        public async Task DownloadPhotoByUrlAsync(string downloadUrl, string localFilePath)
        {
            using (HttpClient client = new HttpClient())
            {
                var response = await client.GetAsync(downloadUrl);
                response.EnsureSuccessStatusCode();

                using (var fs = new FileStream(localFilePath, FileMode.Create, FileAccess.Write))
                {
                    await response.Content.CopyToAsync(fs);
                }
            }
        }

        public async Task UpdatePhotoAsync(PhotoMetadata photo)
        {
            if (string.IsNullOrEmpty(photo.Id))
                throw new ArgumentException("Photo Id is required");

            var docRef = _firestore.Collection("photos").Document(photo.Id);
            await docRef.SetAsync(photo);
        }

        public static void CreateThumbnail(string originalPath, string thumbnailPath, int width, int height)
        {
            using (var image = Image.FromFile(originalPath))
            using (var thumb = image.GetThumbnailImage(width, height, () => false, IntPtr.Zero))
            {
                thumb.Save(thumbnailPath, ImageFormat.Jpeg);
            }
        }

    }
}