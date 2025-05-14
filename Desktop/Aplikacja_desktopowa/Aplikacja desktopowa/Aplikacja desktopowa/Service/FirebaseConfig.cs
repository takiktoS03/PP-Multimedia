using Google.Cloud.Firestore;
using System;
using System.IO;

namespace Aplikacja_desktopowa.Service
{
    public static class FirebaseConfig
    {
        private const string ProjectId = "image-management-cbaee";
        private const string DatabaseId = "image-db";
        private const string CredentialsFile = "image-management-cbaee-firebase-adminsdk-fbsvc-534514b3a5.json";

        public static void Init()
        {
            string path = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, CredentialsFile);
            Environment.SetEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS", path);
        }

        public static FirestoreDb GetFirestoreDb()
        {
            Init();
            return new FirestoreDbBuilder
            {
                ProjectId = ProjectId,
                DatabaseId = DatabaseId
            }.Build();
        }
    }
}
