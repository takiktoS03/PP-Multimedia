using FireSharp;
using Google.Cloud.Firestore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

public class PhotoService
{
    private readonly FirestoreDb _firestore;

    public PhotoService()
    {
        string pathToKey = "image-management-cbaee-firebase-adminsdk-fbsvc-534514b3a5.json"; // Ścieżka do Twojego pliku klucza
        Environment.SetEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS", pathToKey);

        _firestore = FirestoreDb.Create("apka_desktop_id"); // np. "mojprojekt-123456"
    }



    // do przetestowania:
    public async Task AddPhotoAsync(string id, PhotoMetadata photo)
    {
        DocumentReference docRef = _firestore.Collection("photos").Document(id);
        await docRef.SetAsync(photo);
    }

    public async Task<PhotoMetadata> GetPhotoAsync(string id)
    {
        DocumentReference docRef = _firestore.Collection("photos").Document(id);
        DocumentSnapshot snapshot = await docRef.GetSnapshotAsync();

        if (snapshot.Exists)
        {
            return snapshot.ConvertTo<PhotoMetadata>();
        }

        return null;
    }

    public async Task<List<PhotoMetadata>> GetAllPhotosAsync()
    {
        Query query = _firestore.Collection("photos");
        QuerySnapshot snapshot = await query.GetSnapshotAsync();

        List<PhotoMetadata> result = new List<PhotoMetadata>();
        foreach (DocumentSnapshot doc in snapshot.Documents)
        {
            if (doc.Exists)
            {
                result.Add(doc.ConvertTo<PhotoMetadata>());
            }
        }

        return result;
    }
}