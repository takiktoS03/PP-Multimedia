using Aplikacja_desktopowa.Model;
using Google.Cloud.Firestore;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Aplikacja_desktopowa.Service
{
    public class AlbumService
    {
        private readonly FirestoreDb _firestore;

        public AlbumService()
        {
            _firestore = FirebaseConfig.GetFirestoreDb();
        }

        public async Task<List<(string Id, Album Album)>> GetAllAlbumsAsync()
        {
            var albums = new List<(string, Album)>();
            var snapshot = await _firestore.Collection("albums").GetSnapshotAsync();
            foreach (var doc in snapshot.Documents)
            {
                if (doc.Exists)
                    albums.Add((doc.Id, doc.ConvertTo<Album>()));
            }
            return albums;
        }

        public async Task<List<(string Id, Album Album)>> GetAlbumsByUserIdAsync(string userId)
        {
            var query = _firestore.Collection("albums").WhereEqualTo("user_id", userId);
            var snapshot = await query.GetSnapshotAsync();

            var albums = new List<(string, Album)>();
            foreach (var doc in snapshot.Documents)
            {
                if (doc.Exists)
                {
                    albums.Add((doc.Id, doc.ConvertTo<Album>()));
                }
            }
            return albums;
        }

        public async Task<List<string>> GetPhotoIdsForAlbumAsync(string albumId)
        {
            var photoIds = new List<string>();
            var snapshot = await _firestore.Collection("album_photos")
                .WhereEqualTo("album_id", albumId)
                .GetSnapshotAsync();

            foreach (var doc in snapshot.Documents)
            {
                if (doc.Exists && doc.ContainsField("photo_id"))
                    photoIds.Add(doc.GetValue<string>("photo_id"));
            }
            return photoIds;
        }

        public async Task AddPhotoToAlbumAsync(string albumId, string photoId)
        {
            var docRef = _firestore.Collection("album_photos").Document();
            await docRef.SetAsync(new Dictionary<string, object>
            {
                { "album_id", albumId },
                { "photo_id", photoId }
            });
        }

        public async Task RemovePhotoFromAlbumAsync(string albumId, string photoId)
        {
            var query = _firestore.Collection("album_photos")
                .WhereEqualTo("album_id", albumId)
                .WhereEqualTo("photo_id", photoId);

            var snapshot = await query.GetSnapshotAsync();
            foreach (var doc in snapshot.Documents)
            {
                await doc.Reference.DeleteAsync();
            }
        }

        public async Task<string> AddAlbumAsync(string name, string description, string userId)
        {
            var docRef = _firestore.Collection("albums").Document();
            var album = new Album
            {
                Name = name,
                Description = description,
                UserId = userId,
                CreatedAt = DateTime.UtcNow
            };
            await docRef.SetAsync(album);
            return docRef.Id;
        }

        public async Task DeleteAlbumAsync(string albumId)
        {
            await _firestore.Collection("albums").Document(albumId).DeleteAsync();
            // Usuñ powi¹zania album_photos
            var query = _firestore.Collection("album_photos").WhereEqualTo("album_id", albumId);
            var snapshot = await query.GetSnapshotAsync();
            foreach (var doc in snapshot.Documents)
                await doc.Reference.DeleteAsync();
        }

    }
}
