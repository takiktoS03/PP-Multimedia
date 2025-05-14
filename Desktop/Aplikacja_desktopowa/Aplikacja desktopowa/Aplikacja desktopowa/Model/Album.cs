using Google.Cloud.Firestore;

namespace Aplikacja_desktopowa.Model
{
    [FirestoreData]
    public class Album
    {
        [FirestoreProperty(Name = "name")]
        public string Name { get; set; }
    }
}
