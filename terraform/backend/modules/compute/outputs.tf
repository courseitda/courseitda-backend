output "key_pair_name" {
  description = "Name of the EC2 Key Pair"
  value       = aws_key_pair.key_pair.key_name
}

output "private_key_file_path" {
  description = "Path to the private key file generated locally"
  value       = local_file.private_key.filename
}

output "s3_bucket_name" {
  description = "S3 bucket name where private key is stored"
  value       = aws_s3_bucket.key_storage.bucket
}

output "s3_key_path" {
  description = "S3 object key path for the private key"
  value       = aws_s3_object.private_key.key
}
