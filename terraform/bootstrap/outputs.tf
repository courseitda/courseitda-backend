output "s3_buckets" {
  value = {
    for env, bucket in aws_s3_bucket.bucket_tf_state : env => bucket.bucket
  }
}
