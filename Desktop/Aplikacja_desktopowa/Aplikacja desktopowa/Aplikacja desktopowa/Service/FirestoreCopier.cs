using Google.Cloud.Firestore;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Aplikacja_desktopowa.Service
{
    class FirestoreCopier
    {
        private FirestoreDb sourceDb;
        private FirestoreDb targetDb;

        public async Task CopyAsync()
        {
            //  Plik z uprawnieniami
            string credentialsPath = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "image-management-cbaee-firebase-adminsdk-fbsvc-5499d8a881.json");
            Environment.SetEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS", credentialsPath);

            // Połączenie z bazą image-db (źródłowa)
            sourceDb = new FirestoreDbBuilder
            {
                ProjectId = "image-management-cbaee",
                DatabaseId = "image-db"
            }.Build();

            //  Połączenie z bazą default (docelowa)
            targetDb = new FirestoreDbBuilder
            {
                ProjectId = "image-management-cbaee",
                DatabaseId = "(default)"
            }.Build();

            //  Nazwy kolekcji do kopiowania (możesz dodać więcej)
            string[] collections = new string[] { "users"};

            foreach (string collection in collections)
            {
                await CopyCollectionAsync(collection);
            }

            //Console.WriteLine("Kopiowanie zakończone!");
        }

        private async Task CopyCollectionAsync(string collectionName)
        {
            //Console.WriteLine($"Kopiowanie kolekcji: {collectionName}");

            CollectionReference sourceCollection = sourceDb.Collection(collectionName);
            CollectionReference targetCollection = targetDb.Collection(collectionName);

            QuerySnapshot snapshot = await sourceCollection.GetSnapshotAsync();

            foreach (DocumentSnapshot doc in snapshot.Documents)
            {
                Dictionary<string, object> data = doc.ToDictionary();
                await targetCollection.Document(doc.Id).SetAsync(data);
            }
        }
    }
}
