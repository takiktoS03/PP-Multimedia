using Google.Cloud.Firestore;
using System;
using System.Collections.Generic;

namespace Aplikacja_desktopowa.Model
{
    /// <summary>
    /// Reprezentuje metadane zdjęcia przechowywanego w Firebase.
    /// </summary>
    [FirestoreData]
    public class PhotoMetadata
    {
        /// <summary>
        /// Unikalny identyfikator zdjęcia.
        /// </summary>
        [FirestoreProperty(Name = "id")]
        public string Id { get; set; }

        /// <summary>
        /// Publiczny URL zdjęcia w Firebase Storage.
        /// </summary>
        [FirestoreProperty(Name = "file_path")]
        public string FilePath { get; set; }

        /// <summary>
        /// Tytuł zdjęcia nadany przez użytkownika.
        /// </summary>
        [FirestoreProperty(Name = "title")]
        public string Title { get; set; }

        /// <summary>
        /// Opis zdjęcia nadany przez użytkownika.
        /// </summary>
        [FirestoreProperty(Name = "description")]
        public string Description { get; set; }

        /// <summary>
        /// Lokalizacja (miejsce wykonania zdjęcia), jeśli podana.
        /// </summary>
        [FirestoreProperty(Name = "location")]
        public string Location { get; set; }

        /// <summary>
        /// Lista tagów przypisanych do zdjęcia (np. "wakacje", "rodzina").
        /// </summary>
        [FirestoreProperty(Name = "tags")]
        public List<string> Tags { get; set; } = new List<string>();

        /// <summary>
        /// Data i godzina przesłania zdjęcia do systemu.
        /// </summary>
        [FirestoreProperty(Name = "uploaded_at")]
        public DateTime UploadedAt { get; set; }

        /// <summary>
        /// Identyfikator użytkownika, który dodał zdjęcie.
        /// </summary>
        [FirestoreProperty(Name = "user_id")]
        public string UserId { get; set; }
    }
}
