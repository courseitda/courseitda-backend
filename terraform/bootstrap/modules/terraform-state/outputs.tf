output "s3_buckets" {
  value = {
    for env, bucket in aws_s3_bucket.bucket : env => bucket.bucket
  }
}
