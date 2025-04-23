using Google.Cloud.Firestore;
using System;
using System.Collections.Generic;
using System.IO;
using System.Threading.Tasks;

public class PhotoViewModel
{
    private readonly FirestoreDb _firestore;

    public PhotoViewModel()
    {
        string path = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "image-management-cbaee-firebase-adminsdk-fbsvc-5499d8a881.json");
        Environment.SetEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS", path);
        _firestore = FirestoreDb.Create("apka_desktop_id");
    }

    public async Task<List<PhotoMetadata>> GetAllPhotosAsync()
    {
        var snapshot = await _firestore.Collection("photos").GetSnapshotAsync();
        var photos = new List<PhotoMetadata>();

        foreach (var doc in snapshot.Documents)
        {
            if (doc.Exists)
            {
                photos.Add(doc.ConvertTo<PhotoMetadata>());
            }
        }

        return photos;
    }
}
