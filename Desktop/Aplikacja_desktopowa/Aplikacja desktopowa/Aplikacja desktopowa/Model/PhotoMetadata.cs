using Google.Cloud.Firestore;
using System;
using System.Collections.Generic;

namespace Aplikacja_desktopowa.Model
{
    [FirestoreData]
    public class PhotoMetadata
    {
        [FirestoreProperty(Name = "id")]
        public string Id { get; set; }

        [FirestoreProperty(Name = "file_path")]
        public string FilePath { get; set; }

        [FirestoreProperty(Name = "title")]
        public string Title { get; set; }

        [FirestoreProperty(Name = "description")]
        public string Description { get; set; }

        [FirestoreProperty(Name = "location")]
        public string Location { get; set; }

        [FirestoreProperty(Name = "tags")]
        public List<string> Tags { get; set; } = new List<string>();

        [FirestoreProperty(Name = "uploaded_at")]
        public DateTime UploadedAt { get; set; }
    }
}
