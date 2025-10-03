output "key_pair_name" {
  description = "Name of the EC2 Key Pair"
  value       = aws_key_pair.key_pair.key_name
}

output "private_key_file" {
  description = "Path to the private key file generated locally"
  value       = local_file.private_key.filename
}
