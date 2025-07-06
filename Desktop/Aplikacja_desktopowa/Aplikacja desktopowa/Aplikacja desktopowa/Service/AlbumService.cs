using Aplikacja_desktopowa.Model;
using Google.Cloud.Firestore;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Aplikacja_desktopowa.Service
{
    /// <summary>
    /// Serwis do zarz¹dzania albumami u¿ytkowników oraz ich powi¹zaniami ze zdjêciami w Firestore.
    /// </summary>
    public class AlbumService
    {
        private readonly FirestoreDb _firestore;

        /// <summary>
        /// Inicjalizuje now¹ instancjê <see cref="AlbumService"/> i ³¹czy siê z baz¹ Firestore.
        /// </summary>
        public AlbumService()
        {
            _firestore = FirebaseConfig.GetFirestoreDb();
        }

        /// <summary>
        /// Pobiera wszystkie albumy z kolekcji "albums".
        /// </summary>
        /// <returns>Lista krotek zawieraj¹cych ID dokumentu i dane albumu.</returns>
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

        /// <summary>
        /// Pobiera wszystkie albumy przypisane do konkretnego u¿ytkownika.
        /// </summary>
        /// <param name="userId">Identyfikator u¿ytkownika.</param>
        /// <returns>Lista krotek z ID dokumentów i albumami u¿ytkownika.</returns>
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

        /// <summary>
        /// Pobiera identyfikatory zdjêæ przypisanych do danego albumu.
        /// </summary>
        /// <param name="albumId">Identyfikator albumu.</param>
        /// <returns>Lista ID zdjêæ.</returns>
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

        /// <summary>
        /// Dodaje zdjêcie do albumu w kolekcji "album_photos".
        /// </summary>
        /// <param name="albumId">Identyfikator albumu.</param>
        /// <param name="photoId">Identyfikator zdjêcia.</param>
        public async Task AddPhotoToAlbumAsync(string albumId, string photoId)
        {
            var docRef = _firestore.Collection("album_photos").Document();
            await docRef.SetAsync(new Dictionary<string, object>
            {
                { "album_id", albumId },
                { "photo_id", photoId }
            });
        }

        /// <summary>
        /// Usuwa zdjêcie z albumu na podstawie albumId i photoId.
        /// </summary>
        /// <param name="albumId">Identyfikator albumu.</param>
        /// <param name="photoId">Identyfikator zdjêcia.</param>
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

        /// <summary>
        /// Tworzy nowy album w Firestore.
        /// </summary>
        /// <param name="name">Nazwa albumu.</param>
        /// <param name="description">Opis albumu.</param>
        /// <param name="userId">Identyfikator u¿ytkownika.</param>
        /// <returns>Identyfikator utworzonego albumu.</returns>
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

        /// <summary>
        /// Usuwa album i wszystkie jego powi¹zania ze zdjêciami.
        /// </summary>
        /// <param name="albumId">Identyfikator albumu do usuniêcia.</param>
        public async Task DeleteAlbumAsync(string albumId)
        {
            await _firestore.Collection("albums").Document(albumId).DeleteAsync();

            var query = _firestore.Collection("album_photos").WhereEqualTo("album_id", albumId);
            var snapshot = await query.GetSnapshotAsync();
            foreach (var doc in snapshot.Documents)
                await doc.Reference.DeleteAsync();
        }
    }
}
