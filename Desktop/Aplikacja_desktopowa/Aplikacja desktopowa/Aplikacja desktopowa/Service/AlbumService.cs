using Aplikacja_desktopowa.Model;
using Google.Cloud.Firestore;
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
    }
}
