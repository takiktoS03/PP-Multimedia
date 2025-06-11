using Google.Cloud.Firestore;
using System;

namespace Aplikacja_desktopowa.Model
{
    [FirestoreData]
    public class Album
    {
        [FirestoreProperty(Name = "name")]
        public string Name { get; set; }

        [FirestoreProperty(Name = "description")]
        public string Description { get; set; }

        [FirestoreProperty(Name = "created_at")]
        public DateTime CreatedAt { get; set; }
    }
}
