using Google.Cloud.Firestore;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

public class PhotoViewModel
{
    private readonly FirestoreDb _firestore;

    public PhotoViewModel()
    {
        string path = "image-management-cbaee-firebase-adminsdk-fbsvc-534514b3a5.json";
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
