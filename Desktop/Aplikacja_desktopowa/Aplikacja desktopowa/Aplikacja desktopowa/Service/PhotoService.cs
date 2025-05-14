using Aplikacja_desktopowa.Model;
using FireSharp;
using Google.Cloud.Firestore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
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

    }
}