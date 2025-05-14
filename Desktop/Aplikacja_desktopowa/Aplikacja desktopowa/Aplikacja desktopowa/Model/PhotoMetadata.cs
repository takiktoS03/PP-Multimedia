using System;
using Google.Cloud.Firestore;

namespace Aplikacja_desktopowa.Model
{
    [FirestoreData]
    public class PhotoMetadata
    {
        [FirestoreProperty]
        public string FileName { get; set; }

        [FirestoreProperty]
        public double Latitude { get; set; }

        [FirestoreProperty]
        public double Longitude { get; set; }

        [FirestoreProperty]
        public DateTime DateTaken { get; set; }

        [FirestoreProperty(Name = "file_path")]
        public string FilePath { get; set; }
    }
}
