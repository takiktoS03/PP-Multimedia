using Google.Cloud.Firestore;
using System;

namespace Aplikacja_desktopowa.Model
{
    /// <summary>
    /// Reprezentuje album zdjêæ stworzony przez u¿ytkownika w aplikacji.
    /// </summary>
    [FirestoreData]
    public class Album
    {
        /// <summary>
        /// Nazwa albumu nadana przez u¿ytkownika.
        /// </summary>
        [FirestoreProperty(Name = "name")]
        public string Name { get; set; }

        /// <summary>
        /// Opcjonalny opis albumu podany przez u¿ytkownika.
        /// </summary>
        [FirestoreProperty(Name = "description")]
        public string Description { get; set; }

        /// <summary>
        /// Data i godzina utworzenia albumu.
        /// </summary>
        [FirestoreProperty(Name = "created_at")]
        public DateTime CreatedAt { get; set; }

        /// <summary>
        /// Identyfikator u¿ytkownika, do którego nale¿y album.
        /// </summary>
        [FirestoreProperty(Name = "user_id")]
        public string UserId { get; set; }
    }
}
